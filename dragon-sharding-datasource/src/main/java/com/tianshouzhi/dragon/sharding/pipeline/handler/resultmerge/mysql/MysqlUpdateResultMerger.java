package com.tianshouzhi.dragon.sharding.pipeline.handler.resultmerge.mysql;

import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import com.tianshouzhi.dragon.sharding.pipeline.handler.resultmerge.ResultMerger;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 增删改结果合并
 */
public class MysqlUpdateResultMerger implements ResultMerger{
    @Override
    public void merge(HandlerContext context) throws SQLException{
        int totalUpdateCount = 0;
        List<Statement> realStatementList = context.getRealStatementList();
        for (Statement realStatement : realStatementList) {
            totalUpdateCount = totalUpdateCount + realStatement.getUpdateCount();
        }
        context.setTotalUpdateCount(totalUpdateCount);
    }
}
