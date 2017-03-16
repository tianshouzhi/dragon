package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.mysql;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRouteInfo;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRouteParams;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    protected void doRewrite(HandlerContext context) throws SQLException {
        SQLSelectStatement selectStatement= (SQLSelectStatement) sqlAst;

        SQLSelect select = selectStatement.getSelect();

        MySqlSelectQueryBlock query = (MySqlSelectQueryBlock) select.getQuery();

        //解析select item中有别名的情况，主要用于处理order by、group by时使用的不是别名的情况，因为获取值是根据columnLabel来的，如果有alias，columnLabel就是alias，如果没有columnLabel就是columnName
        List<SQLSelectItem> selectList = query.getSelectList();
        Map<String,String> fullColumnAliasMap=null;
        for (SQLSelectItem sqlSelectItem : selectList) {
            String alias = sqlSelectItem.getAlias();
            if(alias !=null){
                //可能直接是列名，也可能是表名.列名
                String fullColumnName = sqlSelectItem.getExpr().toString();
                if(fullColumnAliasMap==null){
                    fullColumnAliasMap=new HashMap<String, String>();
                }
                fullColumnAliasMap.put(fullColumnName,alias);
            }
        }
        context.setFullColumnNameAliasMap(fullColumnAliasMap);

        SQLTableSource tableSource = query.getFrom();
        parseLogicTableList(tableSource);

        SQLExpr where = query.getWhere();
        //解析where条件，只解析可作为路由条件的参数，例如emp.dept_id=dept.id and emp.id=？，只会得到emp.id=？
        List<SQLExpr> whereConditionList = parseRouteConditionList(where);
        SqlRouteParams sqlRouteParams = new SqlRouteParams();
        fillSqlRouteParams(whereConditionList, sqlRouteParams);
        makeRouteMap(sqlRouteParams);

        //如果同时不为空，说明需要对limit语句进行修改 ,特别的，如果只分到一个库，不需要设置limit为0，查询结果的limit就是正确的
        //需要在merge的时候配合，单库的情况不考虑order by和limit
        Map<String, Map<String, SqlRouteInfo>> sqlRouteMap = context.getSqlRouteMap();
        if(needAlterLimit(query, sqlRouteMap)){ //limt 2,2 从第二位开始，查询2个 也就是 2、3两条记录，start要改为0，end要改为start+end
            //// FIXME: 2017/3/14  limit参数支持占位符
            MySqlSelectQueryBlock.Limit limit = query.getLimit();
            //记录原始的offset和rowcount
            int originStart = ((SQLIntegerExpr) limit.getOffset()).getNumber().intValue();
            context.setOffset(originStart);
            int originEnd = ((SQLIntegerExpr) limit.getRowCount()).getNumber().intValue();
            context.setRowCount(originEnd);

            limit.setOffset(new SQLIntegerExpr(0));
            limit.setRowCount(new SQLIntegerExpr(originStart+originEnd));

        }
        makeupSqlRouteInfoSqls();
    }

    private boolean needAlterLimit(MySqlSelectQueryBlock query, Map<String, Map<String, SqlRouteInfo>> sqlRouteMap) {
        if(query.getLimit()==null){
            return false;
        }

        int realSqlSize=0;
        for (Map<String, SqlRouteInfo> sqlRouteInfoMap : sqlRouteMap.values()) {
            realSqlSize+=sqlRouteInfoMap.size();
        }
        if(realSqlSize==1){//只有一条sql要路由，数据库直接完成，不需要，可以不修改
            return false;
        }
        //realSqlSize>1 ,需要到多个表查询，order by应该是必须指定的，否则只指定limit，因为多个表查出来的结果是随机合并的，会导致每次显示的结果不同
        if(query.getOrderBy()==null){
            throw new RuntimeException("sql which only route to one real table can ignore order by clause!!!");
        }
        if(query.getOrderBy().getItems().size()>1){
            throw new RuntimeException("group by only support one column!!!");
        }

        return true;
    }
}
