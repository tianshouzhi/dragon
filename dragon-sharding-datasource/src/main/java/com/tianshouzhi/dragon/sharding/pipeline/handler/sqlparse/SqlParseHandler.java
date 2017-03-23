package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlparse;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import com.tianshouzhi.dragon.common.cache.DragonCache;
import com.tianshouzhi.dragon.common.cache.DragonCacheBuilder;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingStatement;
import com.tianshouzhi.dragon.sharding.pipeline.Handler;
import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import org.apache.commons.collections.MapUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 解析出sql中的参数和参数值(主要是找出分区字段和分区字段的值)
 */
public class SqlParseHandler implements Handler {
    private  static  DragonCache<String,SQLStatement> dragonCache= DragonCacheBuilder.build(100,1000,50,10, TimeUnit.MINUTES);
    @Override
    public void invoke(HandlerContext context) {
        if(MapUtils.isEmpty(context.getHintMap())){//说明没有hint
            DragonShardingStatement dragonShardingStatement = context.getDragonShardingStatement();
            String sql = dragonShardingStatement.getSql();

            boolean hitCache=true;

            SQLStatement sqlStatement = dragonCache.get(sql);//先从cache中获取，如果没有，则解析

            if(sqlStatement==null){
                hitCache=false;
                long start=System.currentTimeMillis();
                SQLStatementParser sqlStatementParser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
                List<SQLStatement> sqlStatements = sqlStatementParser.parseStatementList();
                if(sqlStatements.size()==1){
                    sqlStatement=sqlStatements.get(0);
                }else{
                    throw new RuntimeException("only support one sql!!");
                }
                context.setSqlParseTimeMillis(System.currentTimeMillis()-start);
                dragonCache.put(sql,sqlStatement); //解析完成之后，翻入cache中
            }
            context.setHitSqlParserCache(hitCache);
            context.setParsedSqlStatement(sqlStatement);
        }
    }
}
