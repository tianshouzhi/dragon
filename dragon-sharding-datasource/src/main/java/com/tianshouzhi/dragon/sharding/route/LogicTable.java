package com.tianshouzhi.dragon.sharding.route;

import com.tianshouzhi.dragon.common.jdbc.datasource.DataSourceIndex;

import java.util.Map;

/**
 * 每个逻辑表 管理了 物理表 ，每个物理表 对应一个读写分离数据源编号
 */
public class LogicTable {
    /*
     *物理表的表名 与 对应的DragonHADatasoce数据源之间的映射关系
     */
    private Map<String,DataSourceIndex> physicalTableHAIndexMap;

    public LogicTable(Map<String, DataSourceIndex> physicalTableHAIndexMap) {
        this.physicalTableHAIndexMap = physicalTableHAIndexMap;
    }

    public Map<String, DataSourceIndex> getPhysicalTableHAIndexMap() {
        return physicalTableHAIndexMap;
    }
}
