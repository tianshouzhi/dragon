package com.tianshouzhi.dragon.sharding.jdbc.datasource;

import com.tianshouzhi.dragon.common.cache.DragonCache;
import com.tianshouzhi.dragon.common.cache.DragonCacheBuilder;
import com.tianshouzhi.dragon.sharding.route.LogicDatasource;
import com.tianshouzhi.dragon.sharding.route.LogicTable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by TIANSHOUZHI336 on 2017/3/25.
 */
public class DragonShardingConfig {
    /**
     * cache中除了缓存sql parser handler中解出来的sql ast
     * 还会对sql rewrite handler中的部分rewrite结果进行进行缓存
     */
    private  DragonCache<Object,Object> cache = DragonCacheBuilder.build(100,2000,50,10, TimeUnit.MINUTES);
    //原始配置信息
    private String appName;
    private LogicDatasource logicDatasource;
    /**
     * key为逻辑表名，value是对应的逻辑表对象
     */
    private Map<String,LogicTable> logicTableMap=new ConcurrentHashMap<String, LogicTable>();
    private ExecutorService executor;
    private int executionTimeout;

    public DragonShardingConfig(String appName, LogicDatasource logicDatasource, Map<String, LogicTable> logicTableMap, ExecutorService executor, int executionTimeout) {
        this.appName = appName;
        this.logicDatasource = logicDatasource;
        this.logicTableMap .putAll(logicTableMap);
        this.executor = executor;
        this.executionTimeout = executionTimeout;
    }

    public String getAppName() {
        return appName;
    }

    public LogicDatasource getLogicDatasource() {
        return logicDatasource;
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

    public void putCache(Object key, Object value){
        cache.put(key,value);
    }
    public <T> T getCache(Object key){
        return (T) cache.get(key);
    }
}
