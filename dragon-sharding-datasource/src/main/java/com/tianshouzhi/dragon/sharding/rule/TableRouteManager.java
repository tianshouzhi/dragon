package com.tianshouzhi.dragon.sharding.rule;

import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2016/12/15.
 */
public class TableRouteManager {
    /**
     * key为逻辑表名，value是对应的逻辑表对象
     */
    private Map<String,TableRoute> logicTableMap;

    public TableRouteManager(Map<String, TableRoute> logicTableMap) {
        this.logicTableMap = logicTableMap;
    }

    public TableRoute getLogicTable(String logicTableName){
        return logicTableMap.get(logicTableName);
    }

}
