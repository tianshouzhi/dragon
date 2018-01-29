package com.tianshouzhi.dragon.shard.pipeline.handler.sqlrewrite.mysql;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.tianshouzhi.dragon.shard.exception.DragonShardException;
import com.tianshouzhi.dragon.shard.pipeline.HandlerContext;

import java.sql.SQLException;

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
    public void doRewrite(HandlerContext context) throws SQLException {
        MySqlDeleteStatement deleteAst= (MySqlDeleteStatement) context.getParsedSqlStatement();
        SQLTableSource tableSource= deleteAst.getTableSource();
        if(!(tableSource instanceof SQLExprTableSource)){
            throw new DragonShardException("don't support Multiple-table delete syntax!!!sql:" + originSql);
        }
        SQLExpr where = deleteAst.getWhere();
       /* if(where==null){
            throw new RuntimeException("delete sql must contains where!!!sql:" + originSql);
        }*/

        parseLogicTableList(tableSource);

        //获得where中包含的所有条件
        parseWhereRouteConditionList(where);
//        SqlRouteParams sqlRouteParams = new SqlRouteParams();
        //填充路由参数map
        fillSqlRouteParams();
        //构造路由表
        makeRouteMap();
        //生成路由表中每个SqlRouteInfo的sql
        makeupSqlRouteInfoSqls();
    }
}
