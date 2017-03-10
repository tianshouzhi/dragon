package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.sqlrewriter.mysql;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
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
 <b>Single-Table Syntax</b>

 DELETE [LOW_PRIORITY] [QUICK] [IGNORE] FROM tbl_name
 [PARTITION (partition_name,...)]
 [WHERE where_condition]
 [ORDER BY ...]
 [LIMIT row_count]

 <b>Multiple-Table Syntax 不支持</b>

 DELETE [LOW_PRIORITY] [QUICK] [IGNORE]
 tbl_name[.*] [, tbl_name[.*]] ...
 FROM table_references
 [WHERE where_condition]
Or:
 DELETE [LOW_PRIORITY] [QUICK] [IGNORE]
 FROM tbl_name[.*] [, tbl_name[.*]] ...
 USING table_references
 [WHERE where_condition]
 </pre>
 */
public class MysqlDeleteStatementRewriter extends AbstractMysqlSqlRewriter {
    @Override
    public Map<String, Map<String, SqlRouteInfo>> doRewrite(HandlerContext context) throws SQLException {
        MySqlDeleteStatement deleteAst= (MySqlDeleteStatement) context.getParsedSqlStatement();
        SQLTableSource tableSource= deleteAst.getTableSource();
        if(!(tableSource instanceof SQLExprTableSource)){
            throw new RuntimeException("don't support Multiple-table delete syntax!!!sql:" + originSql);
        }
        SQLExpr where = deleteAst.getWhere();
        if(where==null){
            throw new RuntimeException("delete sql must contains where!!!sql:" + originSql);
        }

        String logicTableName = ((SQLExprTableSource) tableSource).getExpr().toString();
        LogicTable logicTable = context.getLogicTable(logicTableName);
        Set<String> dbTbShardColumns = logicTable.getDbTbShardColumns();

        //获得where中包含的所有条件
        List<SQLExpr> whereConditionList = parseWhereConditionList(where);
        //二元操作符的分区条件
        Map<String, Object> binaryRouteParamsMap = new HashMap<String, Object>();
        Map<String, List<Object>> sqlInListRouteParamsMap = new HashMap<String, List<Object>>();
        fillRouteParamsMap(dbTbShardColumns, whereConditionList, binaryRouteParamsMap, sqlInListRouteParamsMap);
        makeRouteMap(logicTable, binaryRouteParamsMap, sqlInListRouteParamsMap);
        makeUDRealSql(logicTableName);
        return routeMap;
    }
}
