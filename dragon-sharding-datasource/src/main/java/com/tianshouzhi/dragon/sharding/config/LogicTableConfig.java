package com.tianshouzhi.dragon.sharding.config;

import java.util.List;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2017/3/20.
 */
public class LogicTableConfig {
    private String logicTbName;
    private String tbNameFormat;
    private List<String> dbRouteRules;
    private List<String> tbRouteRules;
    private Map<String,String> realDbTbMapping;//${logic_table_name}_[0000,0001]

    public String getLogicTbName() {
        return logicTbName;
    }

    public void setLogicTbName(String logicTbName) {
        this.logicTbName = logicTbName;
    }

    public String getTbNameFormat() {
        return tbNameFormat;
    }

    public void setTbNameFormat(String tbNameFormat) {
        this.tbNameFormat = tbNameFormat;
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
