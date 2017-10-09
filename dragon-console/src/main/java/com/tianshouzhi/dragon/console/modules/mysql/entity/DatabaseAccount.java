package com.tianshouzhi.dragon.console.modules.mysql.entity;

import com.tianshouzhi.dragon.console.base.BaseEntity;

/**
 * 应用访问数据库的账号/密码配置，不同的应用访问同一个库的账号密码不同
 */
public class DatabaseAccount extends BaseEntity {

	private Long appId;// 应用id

	private Long databaseId;// 数据库id

	private String secretKey;// 客户端访问的密钥

	private String readUserName;// 读账号用户名

	private String readPassword;// 读账号密码

	private String writeUserName;// 写账号用户名

	private String writePassword;// 写账号密码

	public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}

	public Long getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(Long databaseId) {
		this.databaseId = databaseId;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getReadUserName() {
		return readUserName;
	}

	public void setReadUserName(String readUserName) {
		this.readUserName = readUserName;
	}

	public String getReadPassword() {
		return readPassword;
	}

	public void setReadPassword(String readPassword) {
		this.readPassword = readPassword;
	}

	public String getWriteUserName() {
		return writeUserName;
	}

	public void setWriteUserName(String writeUserName) {
		this.writeUserName = writeUserName;
	}

	public String getWritePassword() {
		return writePassword;
	}

	public void setWritePassword(String writePassword) {
		this.writePassword = writePassword;
	}

}
