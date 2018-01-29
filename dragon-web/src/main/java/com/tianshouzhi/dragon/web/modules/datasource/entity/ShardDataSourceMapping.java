package com.tianshouzhi.dragon.web.modules.datasource.entity;

import com.tianshouzhi.dragon.web.common.BaseEntity;
import com.tianshouzhi.dragon.web.modules.datasource.entity.enums.DataSourceType;

/**
 * Created by tianshouzhi on 2017/9/5.
 */
public class ShardDataSourceMapping extends BaseEntity {

	private Long shardDataSourceId;

	private String logicDBName;

	private Long managedDataSourceId;

	private DataSourceType managedDataSourceType;// HA or REAL

	public Long getShardDataSourceId() {
		return shardDataSourceId;
	}

	public void setShardDataSourceId(Long shardDataSourceId) {
		this.shardDataSourceId = shardDataSourceId;
	}

	public Long getManagedDataSourceId() {
		return managedDataSourceId;
	}

	public void setManagedDataSourceId(Long managedDataSourceId) {
		this.managedDataSourceId = managedDataSourceId;
	}

	public DataSourceType getManagedDataSourceType() {
		return managedDataSourceType;
	}

	public void setManagedDataSourceType(DataSourceType managedDataSourceType) {
		if (DataSourceType.HA.equals(managedDataSourceType) || DataSourceType.REAL.equals(managedDataSourceType)) {
			throw new IllegalArgumentException("managedDataSourceType only support HA or REAL");
		}
		this.managedDataSourceType = managedDataSourceType;
	}

	public String getLogicDBName() {
		return logicDBName;
	}

	public void setLogicDBName(String logicDBName) {
		this.logicDBName = logicDBName;
	}
}
