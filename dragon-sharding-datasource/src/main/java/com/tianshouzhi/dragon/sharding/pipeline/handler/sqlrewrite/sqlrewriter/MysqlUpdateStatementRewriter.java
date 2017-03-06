package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.sqlrewriter;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRewiter;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRouteInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
 * <b>Multiple-table syntax:</b>
 *
 * UPDATE [LOW_PRIORITY] [IGNORE] table_references
 * SET col_name1={expr1|DEFAULT} [, col_name2={expr2|DEFAULT}] ...
 * [WHERE where_condition]
 * </pre>
 */
public class MysqlUpdateStatementRewriter implements SqlRewiter {

    @Override
    public Map<String, Map<String, SqlRouteInfo>> rewrite(HandlerContext context) throws SQLException {
        try {
            MySqlUpdateStatement update = (MySqlUpdateStatement) context.getParsedSqlStatement();
            SQLTableSource ts = update.getTableSource();

            if (ts != null && ts.toString().contains(",")) {//多表更新语法
                // nothing to do
            }

            String tableName = update.getTableName().getSimpleName();


            StringBuilder sb = new StringBuilder(150);

            SQLExpr se = update.getWhere();
            // where中有子查询： update company set name='com' where id in (select id from xxx where ...)
            if (se instanceof SQLInSubQueryExpr) { // IN 子查询
                // no thing to do
            }

            String where = null;
            if (update.getWhere() != null)
                where = update.getWhere().toString();

            SQLOrderBy orderBy = update.getOrderBy();
            MySqlSelectQueryBlock.Limit limit = update.getLimit();

            sb.append("update ").append(tableName).append(" set ");
            List<SQLUpdateSetItem> items = update.getItems();
            boolean flag = false;
            for (int i = 0; i < items.size(); i++) {
                SQLUpdateSetItem item = items.get(i);
                String col = item.getColumn().toString();
                String val = item.getValue().toString();

                sb.append(col).append("=");
                if (i != items.size() - 1)
                    sb.append(val).append(",");
                else
                    sb.append(val);
            }

            sb.append(" where ").append(where);

            if (orderBy != null && orderBy.getItems() != null
                    && orderBy.getItems().size() > 0) {
                sb.append(" order by ");
                for (int i = 0; i < orderBy.getItems().size(); i++) {
                    SQLSelectOrderByItem item = orderBy.getItems().get(i);
                    SQLOrderingSpecification os = item.getType();
                    sb.append(item.getExpr().toString());
                    if (i < orderBy.getItems().size() - 1) {
                        if (os != null)
                            sb.append(" ").append(os.toString());
                        sb.append(",");
                    } else {
                        if (os != null)
                            sb.append(" ").append(os.toString());
                    }
                }
            }

            if (limit != null) {      // 分为两种情况： limit 10;   limit 10,10;
                sb.append(" limit ");
                if (limit.getOffset() != null)
                    sb.append(limit.getOffset().toString()).append(",");
                sb.append(limit.getRowCount().toString());
            }

            System.out.println(sb);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
