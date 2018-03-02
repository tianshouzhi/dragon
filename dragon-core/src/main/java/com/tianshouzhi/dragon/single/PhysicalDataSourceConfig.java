package com.tianshouzhi.dragon.single;

import com.tianshouzhi.dragon.common.jdbc.WrapperAdapter;
import com.tianshouzhi.dragon.common.util.StringUtils;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by tianshouzhi on 2018/1/28.
 */
public abstract class PhysicalDataSourceConfig extends WrapperAdapter implements DataSource {
	// 数据库连接参数
	protected String username;

	protected String password;

	protected String url;

	protected String driverClassName;

	// 基本连接池参数
	protected int initPoolSize = 5;

	protected int minPoolSize = 5;

	protected int maxPoolSize = 10;

	protected long checkConnectionTimeout = 3000;// 链接超时时间,ms

	// 连接有效性检测
	protected boolean testOnBorrow = false; // 每次获取链接时，检测链接有效性

	protected boolean testWhenIdle = true;// 空闲时检测连接

	protected long validationInterval = 5 * 60 * 1000; // 链接有效性检测周期，单位ms

	protected int validationTimeout = 3; // 链接有效性检测超时时间，单位s。0秒表示不超时

	protected int minEvictableIdleTime = 5 * 60 * 1000; // 链接最小存活时间 ,默认5分钟

	// 链接初始化时执行的sql
	protected List<String> connectionInitSqls;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public int getInitPoolSize() {
		return initPoolSize;
	}

	public void setInitPoolSize(int initPoolSize) {
		if (initPoolSize < 0) {
			throw new IllegalArgumentException("initPoolSize can't less than 0");
		}
		this.initPoolSize = initPoolSize;
	}

	public int getMinPoolSize() {
		return minPoolSize;
	}

	public void setMinPoolSize(int minPoolSize) {
		if (minPoolSize < 0) {
			throw new IllegalArgumentException("minPoolSize can't less than 0");
		}
		this.minPoolSize = minPoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		if (maxPoolSize < 0) {
			throw new IllegalArgumentException("maxPoolSize can't less than 0");
		}
		this.maxPoolSize = maxPoolSize;
	}

	public long getCheckConnectionTimeout() {
		return checkConnectionTimeout;
	}

	public void setCheckConnectionTimeout(long checkConnectionTimeout) {
		this.checkConnectionTimeout = checkConnectionTimeout;
	}

	public List<String> getConnectionInitSqls() {
		return connectionInitSqls;
	}

	public void setConnectionInitSqls(List<String> connectionInitSqls) {
		if (connectionInitSqls == null || connectionInitSqls.isEmpty()) {
			throw new IllegalArgumentException("connectionInitSqls can't be empty");
		}
		this.connectionInitSqls = connectionInitSqls;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public boolean isTestWhenIdle() {
		return testWhenIdle;
	}

	public void setTestWhenIdle(boolean testWhenIdle) {
		this.testWhenIdle = testWhenIdle;
	}

	public long getValidationInterval() {
		return validationInterval;
	}

	public void setValidationInterval(long validationInterval) {
		if (validationInterval < 0) {
			throw new IllegalArgumentException("validationInterval can't less than 0");
		}
		this.validationInterval = validationInterval;
	}

	public int getMinEvictableIdleTime() {
		return minEvictableIdleTime;
	}

	public void setMinEvictableIdleTime(int minEvictableIdleTime) {
		if (minEvictableIdleTime < 0) {
			throw new IllegalArgumentException("minEvictableIdleTime can't less than 0");
		}
		this.minEvictableIdleTime = minEvictableIdleTime;
	}

	public int getValidationTimeout() {
		return validationTimeout;
	}

	public void setValidationTimeout(int validationTimeout) {
		if (validationTimeout < 0) {
			throw new IllegalArgumentException("validationTimeout can't less than 0");
		}
		this.validationTimeout = validationTimeout;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {

	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {

	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	public void checkConfig() {
		if (StringUtils.isAnyBlank(url, driverClassName)) {
			throw new RuntimeException("url，driverClassName can't empty or blank!!!");
		}
		if (initPoolSize < 0 || minPoolSize < 0 || maxPoolSize < minPoolSize) {
			throw new RuntimeException(
					"initPoolSize，minPoolSize must greater than 0, and maxPoolSize must greater or equals to minPoolSize");
		}

		if (checkConnectionTimeout < 0) {
			throw new RuntimeException("checkConnectionTimeout must greater or equals to 0");
		}

	}
}
