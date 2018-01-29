package com.tianshouzhi.dragon.shard.pipeline.handler.sqlrewrite.mysql;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;

/**
 * Created by TIANSHOUZHI336 on 2017/3/14.
 */
public abstract class DragonDruidASTUtil {
    public static String getColumnName(SQLExpr columnExpr) {
        String columnName=null;
        if(columnExpr instanceof SQLIdentifierExpr){
            columnName=((SQLIdentifierExpr) columnExpr).getName();
        }
        if(columnExpr instanceof SQLPropertyExpr){
            columnName=((SQLPropertyExpr) columnExpr).getSimpleName();
        }
        return columnName;
    }
}
