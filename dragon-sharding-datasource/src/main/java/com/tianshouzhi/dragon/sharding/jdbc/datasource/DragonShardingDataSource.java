package com.tianshouzhi.dragon.sharding.jdbc.datasource;

import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSource;
import com.tianshouzhi.dragon.sharding.jdbc.connection.DragonShardingConnection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public class DragonShardingDataSource extends DragonDataSource {
    private ShardingDataSourceManager shardingDataSourceManager;
    
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return new DragonShardingConnection(username,password,shardingDataSourceManager);
    }
}
