package com.tianshouzhi.dragon.benchmark;

import io.shardingjdbc.core.api.MasterSlaveDataSourceFactory;
import io.shardingjdbc.core.api.config.MasterSlaveRuleConfiguration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tianshouzhi on 2017/11/8.
 */
public class RwSplitDataSourceBenchmark {
    public void dragon_DragonHADataSource() {

    }

    public void tddl_GroupDataSource() {

    }

    public void zebra_GroupDataSource() {

    }

    // 构建读写分离数据源, 读写分离数据源实现了DataSource接口, 可直接当做数据源处理. masterDataSource, slaveDataSource0, slaveDataSource1等为使用DBCP等连接池配置的真实数据源
    public void shardingjdbc_MasterSlaveDataSource() throws SQLException {
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put("masterDataSource", masterDataSource);
        dataSourceMap.put("slaveDataSource0", slaveDataSource0);
        dataSourceMap.put("slaveDataSource1", slaveDataSource1);

// 构建读写分离配置
        MasterSlaveRuleConfiguration masterSlaveRuleConfig = new MasterSlaveRuleConfiguration();
        masterSlaveRuleConfig.setName("ms_ds");
        masterSlaveRuleConfig.setMasterDataSourceName("masterDataSource");
        masterSlaveRuleConfig.getSlaveDataSourceNames().add("slaveDataSource0");
        masterSlaveRuleConfig.getSlaveDataSourceNames().add("slaveDataSource1");

        DataSource dataSource = MasterSlaveDataSourceFactory.createDataSource(dataSourceMap, masterSlaveRuleConfig);
    }
}
