package com.tianshouzhi.dragon.console.base;

import java.util.Date;

/**
 * Created by tianshouzhi on 2017/9/6.
 */
public class BaseEntity {
	protected Long id;

	protected Date gmtCreate;

	protected Date gmtUpdate;

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
}
