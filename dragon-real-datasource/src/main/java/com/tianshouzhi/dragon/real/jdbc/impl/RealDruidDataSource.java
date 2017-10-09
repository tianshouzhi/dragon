package com.tianshouzhi.dragon.real.jdbc.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.tianshouzhi.dragon.real.jdbc.RealDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by tianshouzhi on 2017/9/22.
 */
public class RealDruidDataSource extends RealDataSource<DruidDataSource> {

    public RealDruidDataSource(String index, int readWeight, int writeWeight, DruidDataSource dataSource) {
        super(index, readWeight, writeWeight, dataSource);
    }

    @Override
	public void init() throws SQLException {
		dataSource.init();
	}

	@Override
	public void close() throws SQLException {
		dataSource.close();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		if (username == null && password == null) {
            return dataSource.getConnection();
		}
		return dataSource.getConnection(username, password);
	}

}
