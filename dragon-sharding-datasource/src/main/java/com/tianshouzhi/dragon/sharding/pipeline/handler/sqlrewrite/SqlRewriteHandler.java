package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.tianshouzhi.dragon.sharding.pipeline.Handler;
import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.sqlrewriter.MysqlDeleteStatementRewriter;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.sqlrewriter.MysqlInsertStatementRewriter;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public class SqlRewriteHandler implements Handler {
    @Override
    public void invoke(HandlerContext context) throws SQLException{
        SQLStatement sqlStatement = context.getParsedSqlStatement();
        if(sqlStatement !=null){
            Map<String, Map<String,SqlRouteInfo>> sqlRewiteResult = null;
            if(sqlStatement instanceof SQLInsertStatement){
                sqlRewiteResult= new MysqlInsertStatementRewriter().rewrite(context);
            }else if(sqlStatement instanceof SQLUpdateStatement){
//            parseUpdateInfo((SQLUpdateStatement)sqlStatement);
            }else if(sqlStatement instanceof SQLDeleteStatement){
                sqlRewiteResult=new MysqlDeleteStatementRewriter().rewrite(context);
            }else if(sqlStatement instanceof SQLSelectStatement){
//            parseSelectInfo((SQLSelectStatement)sqlStatement);
            }
            context.setSqlRouteMap(sqlRewiteResult);
        }

    }
}
