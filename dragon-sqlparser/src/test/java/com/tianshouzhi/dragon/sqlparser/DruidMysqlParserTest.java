package com.tianshouzhi.dragon.sqlparser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlExportParameterVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Test;

import java.util.List;

/**
 * Created by TIANSHOUZHI336 on 2016/12/13.
 */
public class DruidMysqlParserTest {
    @Test
    public void testExportParameterVisitor(){
        String sql = "select * from t where id = 3 and name = 'abc'";
        SQLStatementParser sqlStatementParser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
        List<SQLStatement> stmtList = sqlStatementParser.parseStatementList();
        if(stmtList.size()==1){
            SQLStatement sqlStatement = stmtList.get(0);
            if(sqlStatement instanceof SQLInsertStatement){
                SQLInsertStatement sqlInsertStatement = (SQLInsertStatement) sqlStatement;
                String tableName = sqlInsertStatement.getTableName().getSimpleName();
            }else if(sqlStatement instanceof SQLSelectStatement){

            }else if(sqlStatement instanceof SQLUpdateStatement){

            }else if (sqlStatement instanceof SQLDeleteStatement){

            }

        }
//        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);

        StringBuilder out = new StringBuilder();
        ExportParameterVisitor visitor = new MySqlExportParameterVisitor(out);
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }

        String paramteredSql = out.toString();
        System.out.println(paramteredSql);

        List<Object> paramters = visitor.getParameters(); // [3, "abc"]
        for (Object param : paramters) {
            System.out.println(param);
        }
    }
}
