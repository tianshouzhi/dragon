package com.tianshouzhi.dragon.common.initailzer.impl;

import com.tianshouzhi.dragon.common.initailzer.AbstractDataSourceInitailzer;

/**
 * Created by TIANSHOUZHI336 on 2017/3/20.
 */
public class DruidDataSourceInitailzer extends AbstractDataSourceInitailzer {
	@Override
	public String initDatasouceClassName() {
		return "com.alibaba.druid.pool.DruidDataSource";
	}
}
