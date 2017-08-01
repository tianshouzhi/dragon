package com.tianshouzhi.dragon.common.initailzer.impl;

import com.tianshouzhi.dragon.common.initailzer.AbstractDataSourceAdapter;

/**
 * Created by TIANSHOUZHI336 on 2017/3/20.
 */
public class DruidDataSourceAdapter extends AbstractDataSourceAdapter {
	@Override
	public String getClassName() {
		return "com.alibaba.druid.pool.DruidDataSource";
	}
}
