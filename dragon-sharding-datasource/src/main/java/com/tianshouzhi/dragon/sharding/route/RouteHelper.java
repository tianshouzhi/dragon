package com.tianshouzhi.dragon.sharding.route;

import java.util.Map;

/**
 * 需要支持直接根据 主维度表名，分区字段 分区字段值来计算，把计算结果设置到thread local里
 *
 * 考虑，如果获取到了realdb-->realtb的映射关系后，sql语法
 */
public class RouteHelper {
    Router router;
    /**
     * 根据规则动态计算
     * @param primaryTableName
     * @param shardColumnAndValueMap
     */
    public void routeByBinaryCondition(String primaryTableName, Map<String,Object> shardColumnAndValueMap){
        LogicTable logicTable = router.getLogicTable(primaryTableName);
//        if(logicTable)
    }

    public void executeByAll(){

    }

}
