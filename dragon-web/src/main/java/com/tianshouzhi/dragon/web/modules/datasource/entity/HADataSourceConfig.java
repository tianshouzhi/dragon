package com.tianshouzhi.dragon.web.modules.datasource.entity;

import com.tianshouzhi.dragon.web.modules.datasource.entity.enums.DataSourceType;

/**
 * Created by tianshouzhi on 2017/8/18.
 */
public class HADataSourceConfig extends BaseDataSourceConfig {
	private Long databaseId;

	@Override
	public DataSourceType getConfigType() {
		return DataSourceType.HA;
	}

	public Long getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(Long databaseId) {
		this.databaseId = databaseId;
	}
}
