package com.tianshouzhi.dragon.shard.pipeline.handler.sqlrewrite.mysql.visitor;

import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;

/**
 * Created by tianshouzhi on 2017/6/11.
 */
public class MysqlInsertASTVisitor extends AbstractMysqlASTVisitor{

    @Override
    public boolean visit(MySqlInsertStatement x) {
        append("INSERT ");
        if (x.isLowPriority()) {
            append("LOW_PRIORITY ");
        }
        if (x.isDelayed()) {
            append("DELAYED ");
        }

        if (x.isHighPriority()) {
            append("HIGH_PRIORITY ");
        }

        if (x.isIgnore()) {
            append("IGNORE ");
        }

        if (x.isRollbackOnFail()) {
            append("ROLLBACK_ON_FAIL ");
        }
        append("INTO ");

        x.getTableSource().accept(this);

        if (x.getColumns().size() > 0) {
            append(" (");
            for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
                if (i != 0) {
                    append(", ");
                }
                x.getColumns().get(i).accept(this);
            }
            append(")");
        }

        if (!x.getValuesList().isEmpty()) {
            append("VALUES ");
            if (x.getValuesList().size() > 1) {
            }
            for (int i = 0, size = x.getValuesList().size(); i < size; ++i) {
                if (i != 0) {
                    append(",");
                }
                x.getValuesList().get(i).accept(this);
            }
        }

        if (x.getQuery() != null) {
            x.getQuery().accept(this);
        }

        if (x.getDuplicateKeyUpdate().size() != 0) {
            append("ON DUPLICATE KEY UPDATE ");
            for (int i = 0, size = x.getDuplicateKeyUpdate().size(); i < size; ++i) {
                if (i != 0) {
                    append(", ");
                }
                x.getDuplicateKeyUpdate().get(i).accept(this);
            }
        }

        return super.visit(x);
    }

}
