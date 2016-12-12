package com.tianshouzhi.dragon.sharding.pipeline.handler;

import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import com.tianshouzhi.dragon.sharding.jdbc.DragonShardingConnection;
import com.tianshouzhi.dragon.sharding.jdbc.DragonShardingStatement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public class HAStatementBuildHandler implements Handler{
    @Override
    public void invoke(HandlerContext context) {
        DragonShardingStatement dragonShardingStatement = context.getDragonShardingStatement();
        try {
            DragonShardingConnection connection = dragonShardingStatement.getConnection();
            List<DragonHAConnection> dragonHAConnectionList=new ArrayList<DragonHAConnection>();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
