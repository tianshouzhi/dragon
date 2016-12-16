package com.tianshouzhi.dragon.sharding.route;

import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2016/12/15.
 */
public class Router {
    /**
     * key为逻辑表名，value是对应的逻辑表对象
     */
    private Map<String,LogicTable> logicTableMap;

    public Router(Map<String, LogicTable> logicTableMap) {
        this.logicTableMap = logicTableMap;
    }
}
