package com.tianshouzhi.dragon.console.modules.datasource.entity;

import com.tianshouzhi.dragon.console.base.BaseEntity;
import com.tianshouzhi.dragon.console.modules.datasource.entity.enums.DataSourceType;

/**
 * Created by tianshouzhi on 2017/9/6.
 */
public abstract class BaseDataSourceConfig extends BaseEntity {

	protected String dataSourceName;

	public abstract DataSourceType getConfigType();

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

}
