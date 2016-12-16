package com.tianshouzhi.dragon.sharding.rule;

import com.tianshouzhi.dragon.sharding.ha.HADataSourceIndex;

import java.util.HashMap;
import java.util.Map;

/**
 * 一个逻辑表，对应一个需要分库的表
 */
public class TableRoute {
    /**
     * 物理表名 与 HA数据源编号的对应关系
     */
    private Map<String,HADataSourceIndex> phsicalTableHAIndexMap;

    public TableRoute(Map<String, HADataSourceIndex> phsicalTableHAIndexMap) {
        this.phsicalTableHAIndexMap = phsicalTableHAIndexMap;
    }
    public Map<String,HADataSourceIndex> get(String ... pyhicalTableNames){
        Map<String,HADataSourceIndex> result=new HashMap<String, HADataSourceIndex>();
        for (String pyhicalTableName : pyhicalTableNames) {
            HADataSourceIndex haDataSourceIndex = phsicalTableHAIndexMap.get(pyhicalTableName);
            result.put(pyhicalTableName,haDataSourceIndex);
        }
        return result;
    }
}
