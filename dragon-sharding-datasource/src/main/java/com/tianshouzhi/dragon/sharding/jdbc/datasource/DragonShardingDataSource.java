package com.tianshouzhi.dragon.sharding.jdbc.datasource;

import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSource;
import com.tianshouzhi.dragon.sharding.config.DragonShardingConfigParser;
import com.tianshouzhi.dragon.sharding.jdbc.connection.DragonShardingConnection;
import com.tianshouzhi.dragon.sharding.route.LogicDataSource;
import com.tianshouzhi.dragon.sharding.route.LogicTable;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public class DragonShardingDataSource extends DragonDataSource {
    private LogicDataSource logicDataSource;
    /**
     * key为逻辑表名，value是对应的逻辑表对象
     */
    private Map<String,LogicTable> logicTableMap;

    public DragonShardingDataSource(String configFileClassPath) {
        try {
            DragonShardingConfigParser dragonShardingConfigParser = new DragonShardingConfigParser(configFileClassPath);
            this.logicDataSource = dragonShardingConfigParser.getLogicDataSource();
            this.logicTableMap = dragonShardingConfigParser.getLogicTableMap();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public DragonShardingDataSource(LogicDataSource logicDataSource, Map<String,LogicTable> logicTableMap) {
        if(logicDataSource ==null||logicTableMap==null){
            throw new NullPointerException();
        }
        this.logicDataSource = logicDataSource;
        this.logicTableMap = logicTableMap;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return new DragonShardingConnection(username,password,this);
    }
    public LogicDataSource getLogicDataSource() {
        return logicDataSource;
    }

    public Map<String, LogicTable> getLogicTableMap() {
        return logicTableMap;
    }

    public LogicTable getLogicTable(String logicTableName) {
        return logicTableMap.get(logicTableName);
    }

    public DataSource getDataSource(String dbIndex) {
        return logicDataSource.getDatasource(dbIndex);
    }

}
