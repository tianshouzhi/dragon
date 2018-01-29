package com.tianshouzhi.dragon.shard.pipeline.handler.sqlrewrite.mysql.visitor;

import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;

/**
 * Created by tianshouzhi on 2017/6/11.
 */
public class AbstractMysqlASTVisitor extends MySqlASTVisitorAdapter {
    private StringBuilder sqlBuilder=new StringBuilder(100);
    protected void append(String str){
        sqlBuilder.append(str);
    }


}
