package com.tianshouzhi.dragon.console.modules.mysql.entity;

import com.tianshouzhi.dragon.console.modules.mysql.entity.enumeration.Environment;

import java.util.Date;

/**
 * Created by tianshouzhi on 2017/8/18.
 */
public class Cluster {

	private Long id;

	private String name;

	private String description;

	private String databaseVersion;

	private String vip;

	private Environment environment;

	private Date gmtCreate;

	private Date gmtUpdate;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDatabaseVersion() {
		return databaseVersion;
	}

	public void setDatabaseVersion(String databaseVersion) {
		this.databaseVersion = databaseVersion;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public String getVip() {
		return vip;
	}

	public void setVip(String vip) {
		this.vip = vip;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtUpdate() {
		return gmtUpdate;
	}

	public void setGmtUpdate(Date gmtUpdate) {
		this.gmtUpdate = gmtUpdate;
	}

	@Override
	public String toString() {
		return "Cluster{" +
				"id=" + id +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", databaseVersion='" + databaseVersion + '\'' +
				", vip='" + vip + '\'' +
				", environment=" + environment +
				", gmtCreate=" + gmtCreate +
				", gmtUpdate=" + gmtUpdate +
				'}';
	}
}
