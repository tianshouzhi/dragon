package com.tianshouzhi.dragon.console.modules.mysql.entity;

import com.tianshouzhi.dragon.console.base.BaseEntity;

/**
 * Created by tianshouzhi on 2017/8/18.
 */
public class Database extends BaseEntity {

	private Long clusterId;

	private String dbName;

	private String charset;

	private Long dbaUserId;

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

	public Long getDbaUserId() {
		return dbaUserId;
	}

	public void setDbaUserId(Long dbaUserId) {
		this.dbaUserId = dbaUserId;
	}
}
