package com.tianshouzhi.dragon.sharding.rule;

import java.util.HashMap;
import java.util.Map;

/**
 * 一个逻辑表，对应一个需要分库的表
 */
public class TableRoute {
    /**
     * 物理表名 与 HA数据源编号 的对应关系
     */
    private Map<String,String> phsicalTableHAIndexMap;

    public TableRoute(Map<String, String> phsicalTableHAIndexMap) {
        this.phsicalTableHAIndexMap = phsicalTableHAIndexMap;
    }
    public Map<String,String> get(String ... pyhicalTableNames){
        Map<String,String> result=new HashMap<String, String>();
        for (String pyhicalTableName : pyhicalTableNames) {
            String String = phsicalTableHAIndexMap.get(pyhicalTableName);
            result.put(pyhicalTableName,String);
        }
        return result;
    }
}
