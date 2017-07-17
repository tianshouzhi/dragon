package com.tianshouzhi.dragon.sharding.pipeline.handler.execution;

import com.tianshouzhi.dragon.common.jdbc.statement.DragonPrepareStatement;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRouteInfo;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 代表一个拆分后的sql执行任务，例如一个sql 要路由到 2个分表中查询，那么就应该创建两个查询任务进行并行的查询
 *
 * execution task接受一个Datasource类型的参数，而不是connection，主要是考虑任务的提交过程是同步的，执行过程才是异步的。如果在任务提交
 * 过程就准备好connection，由于datasource.getconnection方法是同步的，会导致提交任务的时间变长 因此在每个ExecutionTask中自己通过datasource来获取connection，可以并行获取，提高效率
 *
 */
public class ExecutionTask implements Callable<String> {
	private SqlRouteInfo[] sqlRouteInfos;

	private DataSource ds;

	private boolean isPrepare;

	private Connection connection;

	private boolean autoCommit;

	/**
	 * 一个SqlExecutionTask中传入的多个SqlRouteInfo，都是由同一个connection完成
	 * 如果提供了connection，则使用指定的connection；如果没有提供connection，则从ds中获取一个新的connection
	 * 
	 * @param autoCommit
	 * @param sqlRouteInfos
	 */ //
	public ExecutionTask(boolean isPrepare, boolean autoCommit, Connection connection, DataSource ds,
	      SqlRouteInfo... sqlRouteInfos) {
		this.isPrepare = isPrepare;
		this.connection = connection;
		this.sqlRouteInfos = sqlRouteInfos;
		this.ds = ds;
		this.autoCommit = autoCommit;
	}

	@Override
	public String call() throws Exception {
		long start = System.currentTimeMillis();
		Connection realConnection = connection;
		if (realConnection == null) {
			realConnection = ds.getConnection();
		}
		for (SqlRouteInfo sqlRouteInfo : sqlRouteInfos) {
			realConnection.setAutoCommit(autoCommit);
			Statement statement = null;
			String sql = sqlRouteInfo.getSql().toString();
			if (isPrepare) {// 如果是prepareStatement
				statement = realConnection.prepareStatement(sql); //// FIXME: 2017/3/31 传入用户创建statement传入的参数
				Map<Integer, DragonPrepareStatement.ParamSetting> parameters = sqlRouteInfo.getParameters();
				Iterator<Map.Entry<Integer, DragonPrepareStatement.ParamSetting>> iterator = parameters.entrySet()
				      .iterator();
				while (iterator.hasNext()) {
					Map.Entry<Integer, DragonPrepareStatement.ParamSetting> next = iterator.next();
					Integer parameterIndex = next.getKey();
					DragonPrepareStatement.ParamSetting paramSetting = next.getValue();
					Object[] values = paramSetting.values;
					DragonPrepareStatement.ParamType paramType = paramSetting.paramType;
					DragonPrepareStatement.ParamType.setPrepareStatementParams((PreparedStatement) statement, parameterIndex,
					      values, paramType);
				}
				((PreparedStatement) statement).execute();
			} else {// 如果是statement // FIXME: 2017/3/31 传入用户创建statement的参数
				statement = realConnection.createStatement();
				statement.execute(sql);
			}
			// PreparedStatement preparedStatement = realConnection.prepareStatement(sql);
			sqlRouteInfo.setTargetStatement(statement);
			sqlRouteInfo.setExecutionTimeMillis(System.currentTimeMillis() - start);
		}

		return sqlRouteInfos[0].getRealDBName();
	}
}
