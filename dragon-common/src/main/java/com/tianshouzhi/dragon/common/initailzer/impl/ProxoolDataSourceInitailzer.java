package com.tianshouzhi.dragon.common.initailzer.impl;

import com.tianshouzhi.dragon.common.initailzer.AbstractDataSourceInitailzer;

/**
 * Created by TIANSHOUZHI336 on 2017/3/21.
 */
public class ProxoolDataSourceInitailzer extends AbstractDataSourceInitailzer{
    @Override
    public String initDatasouceClassName() {
        return "org.logicalcobwebs.proxool.ProxoolDataSource";
    }
}
