package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlparse;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import com.tianshouzhi.dragon.common.exception.DragonException;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingPrepareStatement;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingStatement;
import com.tianshouzhi.dragon.sharding.pipeline.Handler;
import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * 解析出sql中的参数和参数值(主要是找出分区字段和分区字段的值)
 */
public class SqlParseHandler implements Handler {
	private static final Logger LOGGER = LoggerFactory.getLogger(SqlParseHandler.class);

	@Override
	public void invoke(HandlerContext context) throws SQLException {
		if (MapUtils.isEmpty(context.getHintMap())) {// 说明没有hint
			DragonShardingStatement dragonShardingStatement = context.getShardingStatement();
			String sql = dragonShardingStatement.getSql();

			boolean hitCache = true;
			SQLStatement sqlStatement = null;
			if (dragonShardingStatement instanceof DragonShardingPrepareStatement) {
				sqlStatement = context.getDragonShardingConfig().getCache(sql);// 先从cache中获取，如果没有，则解析
				if (sqlStatement == null) {
					hitCache = false;
					sqlStatement = parseSqlAST(context, sql);
					context.getDragonShardingConfig().putCache(sql, sqlStatement); // 解析完成之后，翻入cache中
				}
			} else {// 对于statement类型不做cache，因为每次构造的语法树都是不同的，cache效率低
				hitCache = false;
				sqlStatement = parseSqlAST(context, sql);
				LOGGER.warn("sql:'{}' is not prepared,will not cache the parsed ast", sql);
			}
			context.setHitSqlParserCache(hitCache);
			context.setParsedSqlStatement(sqlStatement);
		}
	}

	private SQLStatement parseSqlAST(HandlerContext context, String sql) throws DragonException {
		SQLStatement sqlStatement;
		long start = System.currentTimeMillis();
		SQLStatementParser sqlStatementParser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
		List<SQLStatement> sqlStatements = sqlStatementParser.parseStatementList();
		if (sqlStatements.size() == 1) {
			sqlStatement = sqlStatements.get(0);
		} else {
			throw new DragonException("only support one sql!!");
		}
		context.setSqlParseTimeMillis(System.currentTimeMillis() - start);
		return sqlStatement;
	}
}
