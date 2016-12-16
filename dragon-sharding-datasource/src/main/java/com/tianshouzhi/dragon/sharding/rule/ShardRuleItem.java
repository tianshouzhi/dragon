package com.tianshouzhi.dragon.sharding.rule;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2016/12/15.
 */
public interface ShardRuleItem {
    /**
     * 获得逻辑表的名称
     * @return
     */
     String getLogicTableName();

    /**
     * 逻辑表下面管理的物理表名，与存储的库的映射关系
     * @return
     */
     Map<String,DataSource> getPhysicalTableDataSourceMapping();
}
