package com.tianshouzhi.dragon.sharding.pipeline.handler.resultmerge;

import com.tianshouzhi.dragon.ha.jdbc.statement.DragonHAStatement;
import com.tianshouzhi.dragon.sharding.pipeline.handler.Handler;
import com.tianshouzhi.dragon.sharding.pipeline.handler.HandlerContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public class ResultMergeHandler implements Handler {

    @Override
    public void invoke(HandlerContext context) {
        Set<DragonHAStatement> dragonHAStatements = context.getDragonHAStatements();
        int updateCount=0;
        boolean isQuery=false;
        ResultSet resultSet;

        for (DragonHAStatement dragonHAStatement : dragonHAStatements) {
            try {
                if(!isQuery){
                    updateCount+=dragonHAStatement.getUpdateCount();
                }else{
                    dragonHAStatement.getResultSet();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
