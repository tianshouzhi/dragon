package com.tianshouzhi.dragon.console.modules.datasource_config.entity;

import com.tianshouzhi.dragon.console.base.BaseEntity;

/**
 * Created by tianshouzhi on 2017/9/5.
 */
public class AppConfig extends BaseEntity{

	private String appName;

	private Long devUserId;// 开发负责人

	private String description;// 应用描述

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Long getDevUserId() {
		return devUserId;
	}

	public void setDevUserId(Long devUserId) {
		this.devUserId = devUserId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
