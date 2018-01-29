package com.tianshouzhi.dragon.shard.sqlparser;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;

/**
 * Created by tianshouzhi on 2017/6/11.
 */
public class MysqlOutputVisitorTest {
    public static void main(String[] args) {
        String sql="select * from user";
        SQLStatementParser sqlStatementParser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
        SQLSelectStatement sqlStatement = (SQLSelectStatement) sqlStatementParser.parseStatement();
        StringBuilder appender = new StringBuilder();
        MySqlOutputVisitor visitor = new MySqlOutputVisitor(appender);
        boolean visit = visitor.visit(sqlStatement);
        System.out.println(appender);
    }
}
