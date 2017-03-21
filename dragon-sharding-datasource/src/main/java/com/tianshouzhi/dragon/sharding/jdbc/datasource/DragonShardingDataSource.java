package com.tianshouzhi.dragon.sharding.jdbc.datasource;

import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSource;
import com.tianshouzhi.dragon.sharding.config.DragonShardingConfigParser;
import com.tianshouzhi.dragon.sharding.jdbc.connection.DragonShardingConnection;
import com.tianshouzhi.dragon.sharding.route.LogicDatabase;
import com.tianshouzhi.dragon.sharding.route.LogicTable;
import com.tianshouzhi.dragon.sharding.route.Router;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public class DragonShardingDataSource extends DragonDataSource {

    private Router router;

    public DragonShardingDataSource(String configFileClassPath) {
        try {
            DragonShardingConfigParser dragonShardingConfigParser = new DragonShardingConfigParser(configFileClassPath);
            LogicDatabase logicDatabase = dragonShardingConfigParser.getLogicDatabase();
            HashMap<String, LogicTable> logicTableMap = dragonShardingConfigParser.getLogicTableMap();
            router=new Router(logicDatabase,logicTableMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public DragonShardingDataSource(LogicDatabase logicDatabase, Map<String,LogicTable> logicTableMap) {
         router=new Router(logicDatabase,logicTableMap);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return new DragonShardingConnection(username,password,router);
    }

}
