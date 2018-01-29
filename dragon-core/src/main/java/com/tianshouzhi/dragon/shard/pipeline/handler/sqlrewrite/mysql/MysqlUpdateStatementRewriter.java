package com.tianshouzhi.dragon.shard.pipeline.handler.sqlrewrite.mysql;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.tianshouzhi.dragon.shard.exception.DragonShardException;
import com.tianshouzhi.dragon.shard.pipeline.HandlerContext;

import java.sql.SQLException;
import java.util.List;

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
    public void doRewrite(HandlerContext context) throws SQLException {

        MySqlUpdateStatement update = (MySqlUpdateStatement) context.getParsedSqlStatement();
        SQLTableSource tableSource = update.getTableSource();
        if (tableSource != null && tableSource.toString().contains(",")) {//多表更新语法不支持
            throw new DragonShardException("don't support Multiple-table update syntax!!!sql:" + originSql);
        }

        parseLogicTableList(tableSource);
        SQLExpr where = update.getWhere();
        List<SQLUpdateSetItem> items = update.getItems();
        for (SQLUpdateSetItem sqlUpdateSetItem : items) {
            SQLExpr value = sqlUpdateSetItem.getValue();
            if (isJdbcPlaceHolder(value)) {
                currentParamterIndex++;
            }
            //如果是case when更新
            if(value instanceof SQLCaseExpr){
                parseCaseWhen((SQLCaseExpr) value);
            }
        }
        parseWhereRouteConditionList(where);
        //二元操作符的分区条件
        fillSqlRouteParams();
        makeRouteMap();
//            update.getTableName();
        makeupSqlRouteInfoSqls();

    }

    private void parseCaseWhen(SQLCaseExpr value) {
        List<SQLCaseExpr.Item> itemList = value.getItems();
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
        SQLExpr elseExpr = value.getElseExpr();
        if(isJdbcPlaceHolder(elseExpr)){
            currentParamterIndex++;
        }
    }
}
