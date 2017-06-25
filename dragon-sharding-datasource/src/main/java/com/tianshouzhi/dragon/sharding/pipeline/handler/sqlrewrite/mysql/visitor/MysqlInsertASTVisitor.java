package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.mysql;

import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;

/**
 * Created by tianshouzhi on 2017/6/11.
 */
public class MysqlInsertASTRewriter extends MySqlASTVisitorAdapter{
    private StringBuilder sql=new StringBuilder(100);

    @Override
    public boolean visit(MySqlInsertStatement x) {
        builder.
        return super.visit(x);
    }
}
