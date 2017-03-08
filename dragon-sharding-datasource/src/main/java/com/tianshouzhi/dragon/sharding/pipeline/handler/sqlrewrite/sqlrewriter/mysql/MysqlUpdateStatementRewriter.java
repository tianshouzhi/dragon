package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.sqlrewriter.mysql;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRouteInfo;
import com.tianshouzhi.dragon.sharding.route.LogicTable;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 * <b>Single-table syntax:</b>
 *
 * UPDATE [LOW_PRIORITY] [IGNORE] table_reference
 * SET col_name1={expr1|DEFAULT} [, col_name2={expr2|DEFAULT}] ...
 * [WHERE where_condition]
 * [ORDER BY ...]
 * [LIMIT row_count]
 *
 * <b>Multiple-table syntax:(不支持)</b>
 *
 * UPDATE [LOW_PRIORITY] [IGNORE] table_references
 * SET col_name1={expr1|DEFAULT} [, col_name2={expr2|DEFAULT}] ...
 * [WHERE where_condition]
 * </pre>
 */
public class MysqlUpdateStatementRewriter extends AbstractMysqlSqlRewriter {

    @Override
    public Map<String, Map<String, SqlRouteInfo>> doRewrite(HandlerContext context) {

            MySqlUpdateStatement update = (MySqlUpdateStatement) context.getParsedSqlStatement();
            SQLTableSource ts = update.getTableSource();
            if (ts != null && ts.toString().contains(",")) {//多表更新语法不支持
                throw new RuntimeException("don't support Multiple-table update syntax!!!sql:"+originSql);
            }

            String logicTableName = update.getTableName().getSimpleName();
            LogicTable logicTable = context.getLogicTable(logicTableName);
            Set<String> dbTbShardColumns = logicTable.getDbTbShardColumns();
            StringBuilder sb = new StringBuilder(150);
            SQLExpr where = update.getWhere();
            if(where==null){
                throw new RuntimeException("update sql must contains where!!!sql:"+originSql);
            }
        List<SQLUpdateSetItem> items = update.getItems();
        for (SQLUpdateSetItem item : items) {
            if(isJdbcPlaceHolder(item.getValue())){
                currentParamterIndex++;
            }
        }
        List<SQLExpr> whereConditionList = parseWhereCondition(where);
            //二元操作符的分区条件
            Map<String,Object> binaryShardConditionMap=new HashMap<String, Object>();
            Map<String,List<Object>> sqlInListConditionMap=new HashMap<String, List<Object>>();
            for (SQLExpr conditionItemExpr : whereConditionList) {
                if(conditionItemExpr instanceof SQLBinaryOpExpr){
                    parseBinaryConditionExpr(dbTbShardColumns, binaryShardConditionMap, (SQLBinaryOpExpr) conditionItemExpr);
                }
                if(conditionItemExpr instanceof SQLInListExpr){
                    parseSQLInListExpr(dbTbShardColumns, sqlInListConditionMap, (SQLInListExpr) conditionItemExpr);
                }
                if(conditionItemExpr instanceof SQLBetweenExpr){

                }
                if(conditionItemExpr instanceof SQLCaseExpr){

                }
                if(conditionItemExpr instanceof SQLInSubQueryExpr){

                }
            }
            makeRouteMap(logicTable, binaryShardConditionMap, sqlInListConditionMap);
//            update.getTableName();
            for (Map<String, SqlRouteInfo> dbRouteMap : routeMap.values()) {
                for (SqlRouteInfo tbSqlRouteInfo : dbRouteMap.values()) {
                    String newSql = originSql.replaceAll(logicTableName, tbSqlRouteInfo.getTableName());
                    tbSqlRouteInfo.setSql(newSql);
                    if(isPrepare){
                        tbSqlRouteInfo.getParameters().putAll(originParameters);
                    }
                }
            }
        return routeMap;
    }

    private void makeRouteMap(LogicTable logicTable, Map<String, Object> binaryShardConditionMap, Map<String, List<Object>> sqlInListConditionMap) {
        //where partition=xxx的情况
        if(MapUtils.isNotEmpty(binaryShardConditionMap)&&MapUtils.isEmpty(sqlInListConditionMap)){
            addRouteInfo(logicTable, binaryShardConditionMap);
        }
        //where partition1=xxx and partition2 in(x,x,x)的情况
        if(MapUtils.isNotEmpty(sqlInListConditionMap)){
            for (Map.Entry<String, List<Object>> entry : sqlInListConditionMap.entrySet()) {
                String shardColumn = entry.getKey();
                List<Object> valueList = entry.getValue();
                for (Object value : valueList) {
                    HashMap<String, Object> routeConditionMap = new HashMap<String, Object>();
                    routeConditionMap.put(shardColumn,value);
                    if(MapUtils.isNotEmpty(binaryShardConditionMap)){
                        routeConditionMap.putAll(binaryShardConditionMap);
                    }
                    addRouteInfo(logicTable, routeConditionMap);
                }
            }
        }
    }
}
