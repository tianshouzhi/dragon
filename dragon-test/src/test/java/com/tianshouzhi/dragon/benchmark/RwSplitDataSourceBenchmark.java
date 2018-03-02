package com.tianshouzhi.dragon.benchmark;

import com.alibaba.druid.pool.DruidDataSource;
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
        dataSourceMap.put("masterDataSource", createMaster());
        dataSourceMap.put("slaveDataSource1", createSlave1());
        dataSourceMap.put("slaveDataSource2", createSlave2());

// 构建读写分离配置
        MasterSlaveRuleConfiguration masterSlaveRuleConfig = new MasterSlaveRuleConfiguration();
        masterSlaveRuleConfig.setName("ms_ds");
        masterSlaveRuleConfig.setMasterDataSourceName("masterDataSource");
        masterSlaveRuleConfig.getSlaveDataSourceNames().add("slaveDataSource1");
        masterSlaveRuleConfig.getSlaveDataSourceNames().add("slaveDataSource2");

        DataSource dataSource = MasterSlaveDataSourceFactory.createDataSource(dataSourceMap, masterSlaveRuleConfig);
    }

    public DataSource createMaster() throws SQLException {
        DruidDataSource druidDataSource=new DruidDataSource();
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("");
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/dragon_ha");
        druidDataSource.init();
        return druidDataSource;
    }
    public DataSource createSlave1() throws SQLException {
        DruidDataSource druidDataSource=new DruidDataSource();
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("");
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/dragon_ha");
        druidDataSource.init();
        return druidDataSource;
    }
    public DataSource createSlave2() throws SQLException {
        DruidDataSource druidDataSource=new DruidDataSource();
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("");
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/dragon_ha");
        druidDataSource.init();
        return druidDataSource;
    }
}
