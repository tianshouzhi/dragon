package com.tianshouzhi.dragon.web.modules.cluster.entity;

import com.tianshouzhi.dragon.web.common.BaseEntity;

/**
 * Created by tianshouzhi on 2017/8/18.
 */
public class Database extends BaseEntity {

	private Long clusterId;

	private String dbName;

	private String charset;

	private String description;

	public Long getClusterId() {
		return clusterId;
	}

	public void setClusterId(Long clusterId) {
		this.clusterId = clusterId;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
