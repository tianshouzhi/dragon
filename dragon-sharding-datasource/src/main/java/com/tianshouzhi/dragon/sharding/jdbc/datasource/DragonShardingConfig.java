package com.tianshouzhi.dragon.sharding.jdbc.datasource;

import com.tianshouzhi.dragon.sharding.route.LogicDatasouce;
import com.tianshouzhi.dragon.sharding.route.LogicTable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * Created by TIANSHOUZHI336 on 2017/3/25.
 */
public class DragonShardingConfig {
    //原始配置信息
    private String appName;
    private LogicDatasouce logicDatasouce;
    /**
     * key为逻辑表名，value是对应的逻辑表对象
     */
    private Map<String,LogicTable> logicTableMap=new ConcurrentHashMap<String, LogicTable>();
    private ExecutorService executor;
    private int executionTimeout;

    public DragonShardingConfig(String appName, LogicDatasouce logicDatasouce, Map<String, LogicTable> logicTableMap, ExecutorService executor, int executionTimeout) {
        this.appName = appName;
        this.logicDatasouce = logicDatasouce;
        this.logicTableMap .putAll(logicTableMap);
        this.executor = executor;
        this.executionTimeout = executionTimeout;
    }

    public String getAppName() {
        return appName;
    }

    public LogicDatasouce getLogicDatasouce() {
        return logicDatasouce;
    }

    public Map<String, LogicTable> getLogicTableMap() {
        return logicTableMap;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public int getExecutionTimeout() {
        return executionTimeout;
    }
}
