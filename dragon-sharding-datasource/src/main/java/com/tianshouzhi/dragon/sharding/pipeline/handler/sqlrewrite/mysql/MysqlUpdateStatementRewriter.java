package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.mysql;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRouteInfo;
import com.tianshouzhi.dragon.sharding.route.LogicTable;

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
            throw new RuntimeException("don't support Multiple-table update syntax!!!sql:" + originSql);
        }

        String logicTableName = update.getTableName().getSimpleName();
        LogicTable logicTable = context.getLogicTable(logicTableName);
        Set<String> dbTbShardColumns = logicTable.getDbTbShardColumns();
        SQLExpr where = update.getWhere();
        if (where == null) {
            throw new RuntimeException("update sql must contains where!!!sql:" + originSql);
        }
        List<SQLUpdateSetItem> items = update.getItems();
        for (SQLUpdateSetItem sqlUpdateSetItem : items) {
            SQLExpr value = sqlUpdateSetItem.getValue();
            if (isJdbcPlaceHolder(value)) {
                currentParamterIndex++;
            }
            //如果是case when更新
            if(value instanceof SQLCaseExpr){
                List<SQLCaseExpr.Item> itemList = ((SQLCaseExpr) value).getItems();
                for (SQLCaseExpr.Item item : itemList) {
                    SQLExpr conditionExpr = item.getConditionExpr();
                    if(isJdbcPlaceHolder(conditionExpr)){
                        currentParamterIndex++;
                    }
                    SQLExpr valueExpr = item.getValueExpr();
                    if(isJdbcPlaceHolder(valueExpr)){
                        currentParamterIndex++;
                    }
                }
                SQLExpr elseExpr = ((SQLCaseExpr) value).getElseExpr();
                if(isJdbcPlaceHolder(elseExpr)){
                    currentParamterIndex++;
                }
            }
        }
        List<SQLExpr> whereConditionList = parseWhereConditionList(where);
        //二元操作符的分区条件
        Map<String, Object> binaryRouteParamsMap = new HashMap<String, Object>();
        Map<String, List<Object>> sqlInListRouteParamsMap = new HashMap<String, List<Object>>();
        fillRouteParamsMap(dbTbShardColumns, whereConditionList, binaryRouteParamsMap, sqlInListRouteParamsMap);
        makeRouteMap(logicTable, binaryRouteParamsMap, sqlInListRouteParamsMap);
//            update.getTableName();
        makeUDRealSql(logicTableName);
        return routeMap;
    }


}
