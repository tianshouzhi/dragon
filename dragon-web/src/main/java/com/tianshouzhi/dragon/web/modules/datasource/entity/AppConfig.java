package com.tianshouzhi.dragon.web.modules.datasource.entity;

import com.tianshouzhi.dragon.web.common.BaseEntity;

/**
 * Created by tianshouzhi on 2017/9/5.
 */
public class AppConfig extends BaseEntity {

	private String appName;

	private String devUserIds;// 开发负责人

	private String description;// 应用描述

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getDevUserIds() {
		return devUserIds;
	}

	public void setDevUserIds(String devUserIds) {
		this.devUserIds = devUserIds;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
