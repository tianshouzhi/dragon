package com.tianshouzhi.dragon.ha.exception;

import com.tianshouzhi.dragon.ha.jdbc.datasource.RealDataSourceWrapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tianshouzhi on 2017/9/22.
 */
public abstract class DataSourceMoinitor {
    public static Map<String,RealDataSourceWrapper> unaviableDataSources=new ConcurrentHashMap<String,RealDataSourceWrapper>();
    public static void addDataSource(RealDataSourceWrapper realDataSourceWrapper){

    }
}
