package com.tianshouzhi.dragon.common.initailzer.impl;

import com.tianshouzhi.dragon.common.initailzer.AbstractDataSourceInitailzer;

/**
 * Created by TIANSHOUZHI336 on 2017/3/21.
 */
public class C3P0DataSourceInitailzer extends AbstractDataSourceInitailzer{
    @Override
    public String initDatasouceClassName() {
        return "com.mchange.v2.c3p0.ComboPooledDataSource";
    }
}
