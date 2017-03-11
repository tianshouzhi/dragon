package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.sqlrewriter.mysql;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRouteInfo;
import com.tianshouzhi.dragon.sharding.route.LogicTable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 * SELECT
     [ALL | DISTINCT | DISTINCTROW ]
     [HIGH_PRIORITY]
     [MAX_STATEMENT_TIME = N]
     [STRAIGHT_JOIN]
     [SQL_SMALL_RESULT] [SQL_BIG_RESULT] [SQL_BUFFER_RESULT]
     [SQL_CACHE | SQL_NO_CACHE] [SQL_CALC_FOUND_ROWS]
     select_expr [, select_expr ...]
     [FROM table_references
     [PARTITION partition_list]
     [WHERE where_condition]
     [GROUP BY {col_name | expr | position}
     [ASC | DESC], ... [WITH ROLLUP]]
     [HAVING where_condition]
     [ORDER BY {col_name | expr | position}
     [ASC | DESC], ...]
     [LIMIT {[offset,] row_count | row_count OFFSET offset}]
     [PROCEDURE procedure_name(argument_list)]
     [INTO OUTFILE 'file_name'
     [CHARACTER SET charset_name]
     export_options
     | INTO DUMPFILE 'file_name'
     | INTO var_name [, var_name]]
     [FOR UPDATE | LOCK IN SHARE MODE]]
 </pre>
 */
public class MysqlSelectStatementRewriter extends AbstractMysqlSqlRewriter {

    @Override
    protected Map<String, Map<String, SqlRouteInfo>> doRewrite(HandlerContext context) throws SQLException {
        SQLSelectStatement selectStatement= (SQLSelectStatement) sqlAst;

        SQLSelect select = selectStatement.getSelect();

        MySqlSelectQueryBlock query = (MySqlSelectQueryBlock) select.getQuery();
        SQLTableSource tableSource = query.getFrom();
        SQLExpr where = query.getWhere();
        List<SQLExpr> whereConditionList = parseWhereConditionList(where);
        String logicTableName = ((SQLExprTableSource) tableSource).getExpr().toString();
        LogicTable logicTable = context.getLogicTable(logicTableName);
        Set<String> dbTbShardColumns = logicTable.getDbTbShardColumns();
        //二元操作符的分区条件
        Map<String, Object> binaryRouteParamsMap = new HashMap<String, Object>();
        Map<String, List<Object>> sqlInListRouteParamsMap = new HashMap<String, List<Object>>();
        fillRouteParamsMap(dbTbShardColumns, whereConditionList, binaryRouteParamsMap, sqlInListRouteParamsMap);
        makeRouteMap(logicTable, binaryRouteParamsMap, sqlInListRouteParamsMap);
        //如果同时不为空，说明需要对limit语句进行修改
        if(query.getOrderBy()!=null&&query.getLimit()!=null){
            MySqlSelectQueryBlock.Limit limit = query.getLimit();
            //记录原始的offset和rowcount
            context.setOffset(((SQLIntegerExpr)limit.getOffset()).getNumber().intValue());
            context.setRowCount(((SQLIntegerExpr)limit.getRowCount()).getNumber().intValue());

            limit.setOffset(new SQLIntegerExpr(0));

        }
        makeUDRealSql(logicTableName);
        return routeMap;
    }
}
