package com.tianshouzhi.dragon.shard.pipeline.handler.execution;

import com.tianshouzhi.dragon.shard.exception.DragonShardException;
import com.tianshouzhi.dragon.shard.jdbc.connection.DragonShardingConnection;
import com.tianshouzhi.dragon.shard.pipeline.Handler;
import com.tianshouzhi.dragon.shard.pipeline.HandlerContext;
import com.tianshouzhi.dragon.shard.pipeline.handler.sqlrewrite.SqlRouteInfo;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public class ExecutionHandler implements Handler {

	@Override
	public void invoke(HandlerContext context) throws SQLException {
		long start = System.currentTimeMillis();

		DragonShardingConnection dragonShardingConnection = context.getShardingStatement().getConnection();
		ExecutorService executor = context.getDragonShardingConfig().getExecutor();
		CompletionService<String> ecs = new ExecutorCompletionService<String>(executor);

        // 因为一个connection可以创建多个statement，在执行当前statement的时候，判断之前的statement中使用的connection在这里是否可以复用
		List<Future> futureList = new ArrayList<Future>();
		// 判断是否开启了事务，如果开启了事务，sql只能路由到一个库中
		if (!dragonShardingConnection.getAutoCommit()) {
            futureList.addAll(submitTask(context, ecs));
        } else {// 如果开启事务，同一个库的所有sql使用一个连接
			futureList.add(submitTransactionTask(context, ecs));
		}

		context.setParallelExecutionTaskNum(futureList.size());

		try {
			for (int i = 0; i < futureList.size(); i++) {
				ecs.take().get();
			}
		} catch (Exception e) {
			for (Future future : futureList) {
				future.cancel(true);
			}
			throw new DragonShardException("execute sql error",e);
		}

		// 将真实connection封装到sharding connection中 ，这个步骤串行执行，因此不能放到中task中 // TODO: 2017/3/19 是否需要精确到表
		Map<String, Set<Connection>> realConnectionMap = dragonShardingConnection.getRealConnectionMap();
		for (Map.Entry<String, Map<String, SqlRouteInfo>> mapEntry : context.getSqlRouteMap().entrySet()) {
			String realDBName = mapEntry.getKey();
			for (SqlRouteInfo routeInfo : mapEntry.getValue().values()) {
				Statement targetStatement = routeInfo.getTargetStatement();
				Connection connection = targetStatement.getConnection();
				Set<Connection> connections = realConnectionMap.get(realDBName);
				if (connections == null) {
					connections = new HashSet<Connection>();
				}
				connections.add(connection);
				realConnectionMap.put(realDBName, connections);
			}
		}
		context.setParallelExecutionTimeMillis(System.currentTimeMillis() - start);
	}

	private List<Future> submitTask(HandlerContext context, CompletionService<String> ecs) throws SQLException {
		List<Future> futureList = new ArrayList<Future>();
		Map<String, Set<Connection>> realConnectionMap = context.getShardingStatement().getConnection()
		      .getRealConnectionMap();
		for (Map.Entry<String, Map<String, SqlRouteInfo>> entry : context.getSqlRouteMap().entrySet()) {
			String realDBName = entry.getKey();
			// 尝试复用connection，因为之前这个connection执行过statment的话，其肯定包含部分真实connection的引用
			Connection connection = null;
			if (realConnectionMap.containsKey(realDBName)) {// fixme 这样会导致一直只用一个connection
				connection = realConnectionMap.get(realDBName).iterator().next();
			}
			final DataSource ds = context.getRealDataSource(realDBName);

			// 不开启事务 每个sql各自使用一个连接去执行，不管操作的表是不是位于同一个库中，并行执行效率高
			Map<String, SqlRouteInfo> tableSqlMap = entry.getValue();
			Iterator<Map.Entry<String, SqlRouteInfo>> iterator = tableSqlMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, SqlRouteInfo> next = iterator.next();
				final SqlRouteInfo sqlRouteInfo = next.getValue();
				ExecutionTask sqlExecutionTask = new ExecutionTask(context.isPrepare(), true, connection, ds, sqlRouteInfo);
				Future<String> future = ecs.submit(sqlExecutionTask);
				futureList.add(future);
			}
		}
		return futureList;
	}

	private Future submitTransactionTask(HandlerContext context, CompletionService<String> ecs) throws SQLException {
		Map<String, Set<Connection>> realConnectionMap = context.getShardingStatement().getConnection()
		      .getRealConnectionMap();
		Map<String, Map<String, SqlRouteInfo>> sqlRouteMap = context.getSqlRouteMap();
		if (sqlRouteMap.size() > 1) {// 多个库肯定不能使用事务
			String originSql = context.getShardingStatement().getSql();
			Set<String> dbNames = sqlRouteMap.keySet();
			throw new UnsupportedOperationException(
			      "only support transaction in one db,sql:" + originSql + " route to :" + dbNames);
		}
		Map.Entry<String, Map<String, SqlRouteInfo>> next = sqlRouteMap.entrySet().iterator().next();
		String realDBName = next.getKey();
		final DataSource ds = context.getRealDataSource(realDBName);
		Connection connection = null;
		if (realConnectionMap.containsKey(realDBName)) {// 总是拿第一个connection当做事务连接
			connection = realConnectionMap.get(realDBName).iterator().next();
		}
		Collection<SqlRouteInfo> values = next.getValue().values();
		SqlRouteInfo[] sqlRouteInfos = new SqlRouteInfo[values.size()];
		values.toArray(sqlRouteInfos);
		ExecutionTask sqlExecutionTask = new ExecutionTask(context.isPrepare(), false, connection, ds, sqlRouteInfos);
		return ecs.submit(sqlExecutionTask);
	}
}
