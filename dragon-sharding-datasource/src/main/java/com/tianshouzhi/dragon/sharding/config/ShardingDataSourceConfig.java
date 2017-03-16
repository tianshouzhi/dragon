package com.tianshouzhi.dragon.sharding.config;

import java.util.List;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2017/3/15.
 */
public class ShardingDataSourceConfig {
    private String logicDbNname;
    private String dbNameFormat;
    private String realDbNameRange;
    private List<LogicTableYamlConfig> logiTableConfigList;
    private LogicTableYamlConfig defaultLogicTableYamlConfig;
    private RealDatasourceConfig realDatasourceConfig;

    public String getLogicDbNname() {
        return logicDbNname;
    }

    public void setLogicDbNname(String logicDbNname) {
        this.logicDbNname = logicDbNname;
    }

    public String getDbNameFormat() {
        return dbNameFormat;
    }

    public void setDbNameFormat(String dbNameFormat) {
        this.dbNameFormat = dbNameFormat;
    }

    public String getRealDbNameRange() {
        return realDbNameRange;
    }

    public void setRealDbNameRange(String realDbNameRange) {
        this.realDbNameRange = realDbNameRange;
    }

    public List<LogicTableYamlConfig> getLogiTableConfigList() {
        return logiTableConfigList;
    }

    public void setLogiTableConfigList(List<LogicTableYamlConfig> logiTableConfigList) {
        this.logiTableConfigList = logiTableConfigList;
    }

    public LogicTableYamlConfig getDefaultLogicTableYamlConfig() {
        return defaultLogicTableYamlConfig;
    }

    public void setDefaultLogicTableYamlConfig(LogicTableYamlConfig defaultLogicTableYamlConfig) {
        this.defaultLogicTableYamlConfig = defaultLogicTableYamlConfig;
    }

    public RealDatasourceConfig getRealDatasourceConfig() {
        return realDatasourceConfig;
    }

    public void setRealDatasourceConfig(RealDatasourceConfig realDatasourceConfig) {
        this.realDatasourceConfig = realDatasourceConfig;
    }

    public static class LogicTableYamlConfig {
        private String logicTableName;
        private String nameFormat;
        private List<String> dbRouteRules;
        private List<String> tbRouteRules;
        private Map<String,String> realDbTbMapping;

        public String getLogicTableName() {
            return logicTableName;
        }

        public void setLogicTableName(String logicTableName) {
            this.logicTableName = logicTableName;
        }

        public String getNameFormat() {
            return nameFormat;
        }

        public void setNameFormat(String nameFormat) {
            this.nameFormat = nameFormat;
        }

        public List<String> getDbRouteRules() {
            return dbRouteRules;
        }

        public void setDbRouteRules(List<String> dbRouteRules) {
            this.dbRouteRules = dbRouteRules;
        }

        public List<String> getTbRouteRules() {
            return tbRouteRules;
        }

        public void setTbRouteRules(List<String> tbRouteRules) {
            this.tbRouteRules = tbRouteRules;
        }

        public Map<String, String> getRealDbTbMapping() {
            return realDbTbMapping;
        }

        public void setRealDbTbMapping(Map<String, String> realDbTbMapping) {
            this.realDbTbMapping = realDbTbMapping;
        }
    }
    public static class RealDatasourceConfig{
        private String className;
        private Map<String,String> properties;
        private Map<String,Map<String,String>> dataSourceList;

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public Map<String, String> getProperties() {
            return properties;
        }

        public void setProperties(Map<String, String> properties) {
            this.properties = properties;
        }

        public Map<String, Map<String, String>> getDataSourceList() {
            return dataSourceList;
        }

        public void setDataSourceList(Map<String, Map<String, String>> dataSourceList) {
            this.dataSourceList = dataSourceList;
        }
    }
}
