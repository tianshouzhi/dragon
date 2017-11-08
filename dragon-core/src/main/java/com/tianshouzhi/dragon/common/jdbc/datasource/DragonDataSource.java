package com.tianshouzhi.dragon.common.jdbc.datasource;

import com.tianshouzhi.dragon.common.exception.DragonException;

import javax.sql.DataSource;

/**
 * Created by tianshouzhi on 2017/10/13.
 */
public interface DragonDataSource extends DataSource, AutoCloseable {
    String getDsName();

    void setDsName(String dsName);

    void init() throws DragonException;

    void close() throws DragonException;
}
