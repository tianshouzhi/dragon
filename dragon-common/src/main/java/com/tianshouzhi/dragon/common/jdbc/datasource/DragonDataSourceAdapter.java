package com.tianshouzhi.dragon.common.jdbc.datasource;

import com.tianshouzhi.dragon.common.jdbc.WrapperAdapter;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * Created by TIANSHOUZHI336 on 2016/11/30.
 */
public abstract class DragonDataSourceAdapter extends WrapperAdapter implements DragonDataSource {

	protected int loginTimeout = 0;

	private PrintWriter logWriter;

	protected boolean init;

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return logWriter;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		logWriter = out;
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		loginTimeout = seconds;
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return loginTimeout;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return getConnection(null, null);
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("getParentLogger");
	}

	@Override
	public void init() throws Exception {
		if(!init){
			synchronized (this){
				if(!init){
					doInit();
				}
			}
		}
	}

	protected abstract void doInit();
}
