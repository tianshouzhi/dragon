package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.tianshouzhi.dragon.common.jdbc.statement.DragonPrepareStatement;
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
    private static final Logger LOGGER= LoggerFactory.getLogger(SqlRewriteHandler.class);
    @Override
    public void invoke(HandlerContext context) throws SQLException{
        long start=System.currentTimeMillis();
        SQLStatement sqlStatement = context.getParsedSqlStatement();
        boolean isQuery=false;
        if(sqlStatement!=null){// 已经对SQLStatement进行过parse
            if(sqlStatement instanceof SQLInsertStatement){
                new MysqlInsertStatementRewriter().rewrite(context);
            }else if(sqlStatement instanceof SQLUpdateStatement){
                new MysqlUpdateStatementRewriter().rewrite(context);
            }else if(sqlStatement instanceof SQLDeleteStatement){
                new MysqlDeleteStatementRewriter().rewrite(context);
            }else if(sqlStatement instanceof SQLSelectStatement){
                new MysqlSelectStatementRewriter().rewrite(context);
                isQuery=true;
            }
        }
        context.setIsQuery(isQuery);
        context.setSqlRewriteTimeMillis(System.currentTimeMillis()-start);
      if(LOGGER.isDebugEnabled()){
          String sql = context.getDragonShardingStatement().getSql();
          Map<String, Map<String, SqlRouteInfo>> sqlRouteMap = context.getSqlRouteMap();

          LOGGER.debug("sql:{} route map:\n{}",sql, makeRouteDebugInfo(sqlRouteMap));
      }
    }

    private static String makeRouteDebugInfo( Map<String, Map<String, SqlRouteInfo>> sqlRouteMap){
        StringBuilder sb=new StringBuilder();
        Iterator<Map.Entry<String, Map<String, SqlRouteInfo>>> iterator = sqlRouteMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Map<String, SqlRouteInfo>> entry = iterator.next();
            String realDBIndex = entry.getKey();
            sb.append("\n-------"+realDBIndex+"---------\n");
            Map<String, SqlRouteInfo> tbSqlInfoMap = entry.getValue();
            for (Map.Entry<String, SqlRouteInfo> sqlRouteInfoEntry : tbSqlInfoMap.entrySet()) {
                String realTBIndex = sqlRouteInfoEntry.getKey();
                SqlRouteInfo sqlRouteInfo = sqlRouteInfoEntry.getValue();
                sb.append(realTBIndex+"\nsql:"+sqlRouteInfo.getSql()+"\n");//sql不要格式化
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
