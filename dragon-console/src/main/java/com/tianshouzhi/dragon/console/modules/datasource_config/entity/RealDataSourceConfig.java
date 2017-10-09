package com.tianshouzhi.dragon.console.modules.datasource_config.entity;

import com.tianshouzhi.dragon.console.modules.datasource_config.entity.enums.DataSourceType;

/**
 * Created by tianshouzhi on 2017/9/5.
 */
public class RealDataSourceConfig extends BaseDataSourceConfig {
	private Long instanceId;
	private Long databaseId;

	@Override
	public DataSourceType getConfigType() {
		return DataSourceType.REAL;
	}

	public Long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}

	public Long getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(Long databaseId) {
		this.databaseId = databaseId;
	}
}
