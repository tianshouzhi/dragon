package com.tianshouzhi.dragon.sharding.config;

import java.util.List;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2017/3/15.
 */
public class DragonShardingConfig {
    private String logicDSName;
    private String logicDSNameFormat;
    private String realDSClass;
    private Map<String,String> defaultDSConfig;
    private Map<String,Map<String,String>> realDSConfigList;
    private LogicTableConfig defaultLogicTableConfig;
    private List<LogicTableConfig> logiTableConfigList;

    public String getLogicDSName() {
        return logicDSName;
    }

    public void setLogicDSName(String logicDSName) {
        this.logicDSName = logicDSName;
    }

    public String getLogicDSNameFormat() {
        return logicDSNameFormat;
    }

    public void setLogicDSNameFormat(String logicDSNameFormat) {
        this.logicDSNameFormat = logicDSNameFormat;
    }

    public String getRealDSClass() {
        return realDSClass;
    }

    public void setRealDSClass(String realDSClass) {
        this.realDSClass = realDSClass;
    }

    public Map<String, String> getDefaultDSConfig() {
        return defaultDSConfig;
    }

    public void setDefaultDSConfig(Map<String, String> defaultDSConfig) {
        this.defaultDSConfig = defaultDSConfig;
    }

    public Map<String, Map<String, String>> getRealDSConfigList() {
        return realDSConfigList;
    }

    public void setRealDSConfigList(Map<String, Map<String, String>> realDSConfigList) {
        this.realDSConfigList = realDSConfigList;
    }

    public LogicTableConfig getDefaultLogicTableConfig() {
        return defaultLogicTableConfig;
    }

    public void setDefaultLogicTableConfig(LogicTableConfig defaultLogicTableConfig) {
        this.defaultLogicTableConfig = defaultLogicTableConfig;
    }

    public List<LogicTableConfig> getLogiTableConfigList() {
        return logiTableConfigList;
    }

    public void setLogiTableConfigList(List<LogicTableConfig> logiTableConfigList) {
        this.logiTableConfigList = logiTableConfigList;
    }
}
