package com.tianshouzhi.dragon.common.initailzer.impl;

import com.tianshouzhi.dragon.common.initailzer.AbstractDataSourceAdapter;

/**
 * Created by TIANSHOUZHI336 on 2017/3/21.
 */
public class C3P0DataSourceAdapter extends AbstractDataSourceAdapter {
	@Override
	public String getClassName() {
		return "com.mchange.v2.c3p0.ComboPooledDataSource";
	}
}
