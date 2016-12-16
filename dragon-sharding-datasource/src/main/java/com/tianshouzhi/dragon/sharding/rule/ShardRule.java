package com.tianshouzhi.dragon.sharding.rule;

/**
 * 一个DragonShardingDataSource对应一个ShardRule，这个shardRule下面管理了
 * 多个逻辑表LogicTable，一个LogicTable就是一个需要进行分库的表，
 * 每个LogicTable对应了多个PhysicalTable，PhysicalTable就是物理数据库中真实的表，每个PhysicalTable都需要存储对应的数据源的编号
 */
public interface ShardRule {

}
