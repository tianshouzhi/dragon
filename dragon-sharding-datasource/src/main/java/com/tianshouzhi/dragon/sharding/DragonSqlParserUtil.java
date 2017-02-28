package com.tianshouzhi.dragon.sharding;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import org.apache.commons.collections.CollectionUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by TIANSHOUZHI336 on 2017/2/20.
 */
public class DragonSqlParserUtil {
    public void parse(String sql) throws SQLException {
        SQLStatementParser sqlStatementParser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
        List<SQLStatement> sqlStatements = sqlStatementParser.parseStatementList();

        if(sqlStatements.size()==1){
            SQLStatement sqlStatement = sqlStatements.get(0);
            if(sqlStatement instanceof SQLInsertStatement){
                parseInsertInfo((SQLInsertStatement)sqlStatement,sql);
            }else if(sqlStatement instanceof SQLUpdateStatement){
                parseUpdateInfo((SQLUpdateStatement)sqlStatement);
            }else if(sqlStatement instanceof SQLDeleteStatement){
                parseDeleteInfo((SQLDeleteStatement)sqlStatement);
            }else if(sqlStatement instanceof SQLSelectStatement){
                parseSelectInfo((SQLSelectStatement)sqlStatement);
            }
        }else{

        }
    }
    private void parseInsertInfo(SQLInsertStatement insertStatement,String sql) throws SQLException{
        String tableName = insertStatement.getTableName().getSimpleName();
        List<SQLExpr> columns = insertStatement.getColumns();
        // insert 没有带列名：insert into t values(xxx,xxx)
        if(CollectionUtils.isEmpty(columns)){
            throw new SQLException("insert sql("+sql+") must contains column names");
        }
    }

    private void parseSelectInfo(SQLSelectStatement sqlStatement) {

    }

    private void parseDeleteInfo(SQLDeleteStatement sqlStatement) {

    }

    private void parseUpdateInfo(SQLUpdateStatement sqlStatement) {

    }


}
