package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlparse;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingStatement;
import com.tianshouzhi.dragon.sharding.pipeline.Handler;
import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import org.apache.commons.collections.MapUtils;

import java.util.List;

/**
 * 解析出sql中的参数和参数值(主要是找出分区字段和分区字段的值)
 */
public class SqlParseHandler implements Handler {
    @Override
    public void invoke(HandlerContext context) {

        if(MapUtils.isEmpty(context.getHintMap())){//说明没有hint
            DragonShardingStatement dragonShardingStatement = context.getDragonShardingStatement();
            String sql = dragonShardingStatement.getSql();
            SQLStatementParser sqlStatementParser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
            List<SQLStatement> sqlStatements = sqlStatementParser.parseStatementList();
            if(sqlStatements.size()==1){
                context.setParsedSqlStatement(sqlStatements.get(0));
            }else{
                throw new RuntimeException("only support one sql!!");
            }
        }
    }
}
