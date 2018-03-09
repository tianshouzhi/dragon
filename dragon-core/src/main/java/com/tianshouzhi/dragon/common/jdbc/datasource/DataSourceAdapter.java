package com.tianshouzhi.dragon.common.jdbc.datasource;

import com.tianshouzhi.dragon.common.exception.DragonException;
import com.tianshouzhi.dragon.common.jdbc.WrapperAdapter;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * Created by TIANSHOUZHI336 on 2016/11/30.
 */
public abstract class DataSourceAdapter extends WrapperAdapter implements DragonDataSource {

	protected int loginTimeout = 0;

	protected PrintWriter logWriter;

	protected volatile boolean init = false;

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

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		init();
		return doGetConnection(username,password);
	}

	protected abstract Connection doGetConnection(String username, String password) throws SQLException;

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("getParentLogger");
	}

	@Override
	public void init() throws DragonException{
		if (!init) {
			synchronized (this) {
				if (!init) {
					try {
						doInit();
					} catch (Exception e) {
						throw new DragonException(e);
					}
				}
				init = true;
			}
		}
	}

	protected abstract void doInit() throws Exception;
}
