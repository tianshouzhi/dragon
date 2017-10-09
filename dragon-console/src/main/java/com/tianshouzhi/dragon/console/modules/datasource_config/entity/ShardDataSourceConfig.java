package com.tianshouzhi.dragon.console.modules.datasource_config.entity;

import com.tianshouzhi.dragon.console.modules.datasource_config.entity.enums.DataSourceType;

/**
 * Created by tianshouzhi on 2017/9/5.
 */
public class ShardDataSourceConfig extends BaseDataSourceConfig {

	private ShardStrategy shardStrategy;

	private NamingStrategy namingStrategy;

	private Integer maxShardDBNum;// 最大支持分库数

	private Integer shardDBNum;// 当前使用分库数

	private Integer maxShardTBNum;// 最大支持分库数

	private Integer shardTBNum;// 当前使用分表数

	@Override
	public DataSourceType getConfigType() {
		return DataSourceType.SHARD;
	}

	public static enum ShardStrategy {
		SHARD_DB, SHARD_TB, SHARD_DB_AND_TB
	}

	public static enum NamingStrategy {
		ALL_DB, EVERY_DB, SCALE_DB
	}
}
