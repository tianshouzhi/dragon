package com.tianshouzhi.dragon.sharding.pipeline.handler.resultmerge;

import com.tianshouzhi.dragon.common.util.SqlTypeUtil;
import com.tianshouzhi.dragon.sharding.pipeline.Handler;
import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRouteInfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public class ResultMergeHandler implements Handler {
    @Override
    public void invoke(HandlerContext context) throws Exception {

        Map<String, Map<String, SqlRouteInfo>> sqlRewriteResult = context.getSqlRouteMap();
        Iterator<Map.Entry<String, Map<String, SqlRouteInfo>>> dbIterator = sqlRewriteResult.entrySet().iterator();
        List<PreparedStatement> preparedStatementList = new ArrayList<PreparedStatement>();
        while (dbIterator.hasNext()) {
            Map.Entry<String, Map<String, SqlRouteInfo>> entry = dbIterator.next();
            String dbIndex = entry.getKey();
            Map<String, SqlRouteInfo> tbSqlMap = entry.getValue();
            Iterator<Map.Entry<String, SqlRouteInfo>> tbIterator = tbSqlMap.entrySet().iterator();
            while (tbIterator.hasNext()) {
                Map.Entry<String, SqlRouteInfo> tableResult = tbIterator.next();
                PreparedStatement targetStatement = tableResult.getValue().getTargetStatement();
                preparedStatementList.add(targetStatement);
            }
        }
        boolean query = SqlTypeUtil.isQuery(context.getDragonShardingStatement().getSql(), true);
        context.setIsQuery(query);
        if (!query) {
            int totalUpdateCount = 0;
            for (PreparedStatement preparedStatement : preparedStatementList) {
                try {
                    totalUpdateCount = totalUpdateCount + preparedStatement.getUpdateCount();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            context.setTotalUpdateCount(totalUpdateCount);
        }else{
            ResultSet resultSet = null;
        }

    }
}
