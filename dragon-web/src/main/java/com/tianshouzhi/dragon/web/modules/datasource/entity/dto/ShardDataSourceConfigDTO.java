package com.tianshouzhi.dragon.web.modules.datasource.entity.dto;

import com.tianshouzhi.dragon.web.modules.datasource.entity.ShardDataSourceConfig;
import com.tianshouzhi.dragon.web.modules.datasource.entity.ShardDataSourceMapping;
import com.tianshouzhi.dragon.web.modules.datasource.entity.ShardRule;

import java.util.Map;

/**
 * Created by tianshouzhi on 2017/9/6.
 */
public class ShardDataSourceConfigDTO extends ShardDataSourceConfig{

	private Map<String, ShardDataSourceMapping> dataSourceMappingMap;

	private Map<String, ShardRule> shardRuleMap;

	public Map<String, ShardDataSourceMapping> getDataSourceMappingMap() {
		return dataSourceMappingMap;
	}

	public void setDataSourceMappingMap(Map<String, ShardDataSourceMapping> dataSourceMappingMap) {
		this.dataSourceMappingMap = dataSourceMappingMap;
	}

	public Map<String, ShardRule> getShardRuleMap() {
		return shardRuleMap;
	}

	public void setShardRuleMap(Map<String, ShardRule> shardRuleMap) {
		this.shardRuleMap = shardRuleMap;
	}
}
