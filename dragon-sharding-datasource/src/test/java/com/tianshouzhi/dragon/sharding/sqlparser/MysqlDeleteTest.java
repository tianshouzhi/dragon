package com.tianshouzhi.dragon.sharding.sqlparser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRouteInfo;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2017/2/28.
 */
public class MysqlDeleteTest {
    @Test
    public void testParseDelete(){
//        String sql="delete from user where id =1 and name=xxx and age=25";
        String sql="DELETE p.*, pp.* \n" +
                "FROM product p, productPrice pp \n" +
                "WHERE p.productId = pp.productId \n" +
                "AND p.created < '2004-01-01' ";
        SQLStatementParser sqlStatementParser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
        MySqlDeleteStatement deleteStatement = (MySqlDeleteStatement) sqlStatementParser.parseStatement();
        StringBuilder sb=new StringBuilder();
        sb.append("DELETE ");
        if(deleteStatement.isLowPriority()){
            sb.append("LOW_PRIORITY ");
        }
        if(deleteStatement.isQuick()){
            sb.append("QUICK ");
        }
        if(deleteStatement.isIgnore()){
            sb.append("IGNORE ");
        }
        SQLTableSource tableSource= deleteStatement.getTableSource();
        if(!(tableSource instanceof SQLExprTableSource)){
            throw new RuntimeException("only support Single-Table delete Syntax table,don't support Multiple-Table delete Syntax!!!");
        }
        sb.append("FROM ");
        String tableName = ((SQLExprTableSource) tableSource).getExpr().toString();

        sb.append(tableName+" ");
        SQLExpr where = deleteStatement.getWhere();
        if(where==null){
            throw new RuntimeException("delete sql must contains where condition!!!");
        }
        if(where!=null){
            Map<String, Map<String, SqlRouteInfo>> split=new HashMap<String, Map<String, SqlRouteInfo>>();
            if(where instanceof SQLBinaryOpExpr){
                SQLExpr left = ((SQLBinaryOpExpr) where).getLeft();
                SQLExpr right = ((SQLBinaryOpExpr) where).getRight();
                SQLBinaryOperator operator = ((SQLBinaryOpExpr) where).getOperator();
                switch (operator) {
                    case Equality:
                        break;
                    case BooleanAnd:
                        break;
                }
            }

        }

    }

    public void recur(SQLBinaryOpExpr sqlBinaryOpExpr,Map<String, Map<String, SqlRouteInfo>> routeInfoMap){
        SQLExpr left = sqlBinaryOpExpr.getLeft();
        SQLExpr right = sqlBinaryOpExpr.getRight();
        if(left instanceof SQLBinaryOpExpr){
            recur((SQLBinaryOpExpr)left,routeInfoMap);
        }
        if(right instanceof SQLBinaryOpExpr){
            recur((SQLBinaryOpExpr)right,routeInfoMap);
        }
        if(left instanceof SQLIdentifierExpr){
            String columnName = ((SQLIdentifierExpr) left).getSimpleName();

        }

    }
}
