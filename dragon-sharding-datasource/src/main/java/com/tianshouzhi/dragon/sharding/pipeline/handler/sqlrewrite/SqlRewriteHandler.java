package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.tianshouzhi.dragon.common.jdbc.statement.DragonPrepareStatement;
import com.tianshouzhi.dragon.sharding.exception.DragonShardException;
import com.tianshouzhi.dragon.sharding.pipeline.Handler;
import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.mysql.MysqlDeleteStatementRewriter;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.mysql.MysqlInsertStatementRewriter;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.mysql.MysqlSelectStatementRewriter;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.mysql.MysqlUpdateStatementRewriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public class SqlRewriteHandler implements Handler {
	private static final Logger LOGGER = LoggerFactory.getLogger(SqlRewriteHandler.class);

	@Override
	public void invoke(HandlerContext context) throws SQLException {
		long start = System.currentTimeMillis();
		SQLStatement sqlStatement = context.getParsedSqlStatement();
		boolean isQuery = false;
		if (sqlStatement != null) {// 已经对SQLStatement进行过parse
			if (sqlStatement instanceof SQLInsertStatement) {
				new MysqlInsertStatementRewriter().rewrite(context);
			} else if (sqlStatement instanceof SQLUpdateStatement) {
				new MysqlUpdateStatementRewriter().rewrite(context);
			} else if (sqlStatement instanceof SQLDeleteStatement) {
				new MysqlDeleteStatementRewriter().rewrite(context);
			} else if (sqlStatement instanceof SQLSelectStatement) {
				new MysqlSelectStatementRewriter().rewrite(context);
				isQuery = true;
			} else {
				throw new DragonShardException("only support insert、delete、update、select statement，current sql："
				      + context.getShardingStatement().getSql());
			}
		}
		context.setIsQuery(isQuery);
		context.setSqlRewriteTimeMillis(System.currentTimeMillis() - start);
		// statics handler会打印出所有的route信息，这里主要是为了开发调试用，因为有些sql重写后，可能执行失败了。
		/*
		 * if(LOGGER.isDebugEnabled()){ LOGGER.debug("\n" +
		 * "===============================route map begin================================:" + "{}\n" +
		 * "=================================route map end================================",
		 * makeRouteDebugInfo(context.getSqlRouteMap())); }
		 */
	}

	private static String makeRouteDebugInfo(Map<String, Map<String, SqlRouteInfo>> sqlRouteMap) {
		StringBuilder sb = new StringBuilder();
		Iterator<Map.Entry<String, Map<String, SqlRouteInfo>>> iterator = sqlRouteMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Map<String, SqlRouteInfo>> entry = iterator.next();
			String realDBIndex = entry.getKey();
			sb.append("\n-------" + realDBIndex + "---------");
			Map<String, SqlRouteInfo> tbSqlInfoMap = entry.getValue();
			for (Map.Entry<String, SqlRouteInfo> sqlRouteInfoEntry : tbSqlInfoMap.entrySet()) {
				SqlRouteInfo sqlRouteInfo = sqlRouteInfoEntry.getValue();
				sb.append("\nsql:" + sqlRouteInfo.getSql() + "\n");// sql不要格式化
				Map<Integer, DragonPrepareStatement.ParamSetting> parameters = sqlRouteInfo.getParameters();
				sb.append("params:");
				for (Map.Entry<Integer, DragonPrepareStatement.ParamSetting> paramSettingEntry : parameters.entrySet()) {
					sb.append(Arrays.toString(paramSettingEntry.getValue().values));
				}

			}
		}
		return sb.toString();
	}
}
