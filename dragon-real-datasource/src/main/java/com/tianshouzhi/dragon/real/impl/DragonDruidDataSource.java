package com.tianshouzhi.dragon.real.impl;

import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSource;
import com.tianshouzhi.dragon.real.DragonRealDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by tianshouzhi on 2017/8/17.
 */
public class DragonDruidDataSource extends DragonDataSource implements DragonRealDataSource {
    @Override
    public void init() throws Throwable {

    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }
}
