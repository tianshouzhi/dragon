package com.tianshouzhi.dragon.real;

import com.alibaba.druid.pool.DruidDataSource;
import com.tianshouzhi.dragon.real.jdbc.RealDataSource;
import com.tianshouzhi.dragon.real.jdbc.impl.RealDruidDataSource;

/**
 * Created by tianshouzhi on 2017/10/9.
 */
public abstract class RealDataSourceFactory {
    public static RealDataSource create(String index, int readWeight, int writeWeight, DruidDataSource dataSource){
        return new RealDruidDataSource(index,readWeight,writeWeight,dataSource);
    }
}
