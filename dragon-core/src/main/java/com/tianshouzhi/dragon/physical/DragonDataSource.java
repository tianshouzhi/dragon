package com.tianshouzhi.dragon.physical;

import com.tianshouzhi.dragon.common.thread.DragonThreadFactory;
import com.tianshouzhi.dragon.common.util.StringUtils;

import java.sql.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by tianshouzhi on 2018/1/23.
 */
public class DragonDataSource extends DragonAbstractDataSource{
	// 数据库连接参数
	private String username;
	private String password;
	private String url;
	private String driverClassName;

	//基本连接池参数
	private int initPoolSize = 5;
	private int minPoolSize = 5;
	private int maxPoolSize = 10;
	private long checkoutTimeout = 1000;// 链接超时时间,ms

	//获取链接相关参数
	private boolean testOnBorrow = false; // 每次获取链接时，检测链接有效性
	private boolean testWhenIdle = true;// 空闲时检测连接
	private List<String> connectionInitSqls;// 链接初始化时执行的sql

	// 连接有效性检测
	private long validationInterval = 5 * 60 * 1000; // 链接有效性检测周期，单位ms
	private int validationTimeout = 3; // 链接有效性检测超时时间，单位s。0秒表示不超时
	private int minEvictableIdleTime = 5 * 60 * 1000; // 链接最小存活时间 ,默认5分钟

	//连接池
	private BlockingQueue<DragonConnection> connectionPool = null;

	private int poolSize;// 当前连接池大小 = idleSize+busySize

	// 状态信息
	private boolean init = false; //连接池是否初始化

	private boolean closed = false; //连接池是否已经关闭

	//异步获取链接的线程池
	private ExecutorService connectionGetter = Executors.newSingleThreadExecutor(new DragonThreadFactory("DRAGON-SINGLE-DATASOURCE-POOL", true));
	
	//链接有效性检测连接池
	private ScheduledExecutorService connectionValidator = Executors.newSingleThreadScheduledExecutor(new DragonThreadFactory("DRAGON-SINGLE-DATASOURCE-POOL", true));

	public void init() throws SQLException{
		if (init) {
			return;
		}
		synchronized (this) {
			if (!init) {
				try {
					checkConfig();
					registerDriver();
					initConnectionPool();
					initConnectionValidator();
					this.init = true;
				} catch (Throwable e) {
					close();
					throw new DragonSQLException("init error",e);
				}
			}
		}
	}
	
	private void checkConfig() {
		if (StringUtils.isAnyBlank(url, driverClassName)) {
			throw new IllegalArgumentException("url，driverClassName can't empty or blank!!!");
		}
		if (initPoolSize < 0 || minPoolSize < 0 || maxPoolSize < minPoolSize) {
			throw new IllegalArgumentException(
					"initPoolSize，minPoolSize must greater than 0, and maxPoolSize must greater or equals to minPoolSize");
		}

		if (checkoutTimeout < 0) {
			throw new IllegalArgumentException("checkoutTimeout must greater or equals to 0");
		}
	}

	private void registerDriver()
			throws Exception {
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			if (driver.getClass().getName().equals(driverClassName)) {
				return;
			}
		}
		DriverManager.registerDriver((Driver) Class.forName(driverClassName).newInstance());
	}

	private void initConnectionPool() throws SQLException{
		connectionPool = new LinkedBlockingQueue<DragonConnection>(maxPoolSize);
		for (int i = 0; i <initPoolSize ; i++) {
			fillConnectionPoolDirect();
		}
	}

	private void initConnectionValidator() {
		if (testWhenIdle) {
			this.connectionValidator.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					Iterator<DragonConnection> iterator = connectionPool.iterator();
					while (iterator.hasNext()) {
						DragonConnection connection = iterator.next();
						// 如果达到最小驱逐时间，且当前连接池大小>最小数，说明连接池比较空闲，则关于多余的链接，节省资源
						if (System.currentTimeMillis() - connection.getLastActiveTime() >= minEvictableIdleTime && poolSize > minPoolSize) {
							connectionPool.remove(connection);
							closeRealConnection(connection);
							continue;
						}
						//如果连接不可用，直接关闭
						try {
							connection.isValid(validationTimeout);
						} catch (SQLException e) {
							closeRealConnection(connection);
						}
					}
				}
			}, 10, validationInterval, TimeUnit.MILLISECONDS);
		}
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		if (closed) {
			throw new DragonSQLException("DragonDataSource is closed,can't get new connection!!!");
		}
		init();
		return doGetConnection(checkoutTimeout);
	}

	private DragonConnection doGetConnection(long timeout) throws SQLException {
		long deadline = System.currentTimeMillis() + checkoutTimeout;
		if (getIdleSize() == 0 && getPoolSize() < maxPoolSize) {
			fillConnectionPoolAsync();
		}

		DragonConnection connection = null;

		try{
			if (checkoutTimeout > 0) {
				connection = connectionPool.poll(timeout, TimeUnit.MILLISECONDS);
				if (connection == null) {
					throw new DragonSQLException("can't get a connection in " + this.checkoutTimeout + " ms!"+this.toString());
				}
			} else {
				connection = connectionPool.take();
			}
		}catch (InterruptedException e){
			throw new DragonSQLException("thread has been interrupted when try to get connection!",e);
		}

		if (testOnBorrow) {
			boolean valid = false;
			try {
				valid = connection.isValid(validationTimeout);
			} catch (Throwable ignore) {
				closeRealConnection(connection);
			}

			if (valid) {
				return connection;
			}

			if (checkoutTimeout == 0) {
				connection = doGetConnection(0);
			} else {
				long remainderTimeout = deadline - System.currentTimeMillis();
				if (remainderTimeout <= 0) {
					throw new DragonSQLException("can't get a connection in " + this.checkoutTimeout + " ms!"+this.toString());
				}
				connection = doGetConnection(remainderTimeout);
			}

		}
		return connection;
	}

	void returnConnection(DragonConnection connection) throws SQLException {
		if (connection == null) {
			return;
		}
		try {
			if (!connection.isClosed()) {
				connectionPool.put(connection);
			} else {
				decrementPoolSize();
			}
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	@Override
	public DragonConnection getConnection(String username, String password) throws SQLException {
		throw new SQLFeatureNotSupportedException("borrowConnection(String username, String password) is not Support!!!");
	}

	private void fillConnectionPoolDirect() throws SQLException {
		synchronized (this){
			if (poolSize >= maxPoolSize) {
				return;
			}
			incrementPoolSize(); // 预先将 poolSize + 1，防止多个线程同时满足条件，导致超过 maxPoolSize 限制
		}
		try {
			Connection realConnection = getRealConnection();
			DragonConnection dragonConnection = new DragonConnection(realConnection, this);
			connectionPool.put(dragonConnection);
		} catch (Throwable e) {
			decrementPoolSize(); // 如果获取链接失败，poolSize - 1
			throw new DragonSQLException("fill connection pool error", e);
		}
	}

	private void fillConnectionPoolAsync() {
		connectionGetter.submit(new Runnable() {
			@Override
			public void run() {
				try {
					fillConnectionPoolDirect();
				} catch (Exception ignore) {

				}
			}
		});
	}

	private Connection getRealConnection() throws SQLException {
		// get real connection
		Connection connection = DriverManager.getConnection(url, username, password);

		// init sqls
		if (connectionInitSqls != null && connectionInitSqls.size() > 0) {
			Statement statement = null;
			try {
				statement = connection.createStatement();
				for (String initSql : connectionInitSqls) {
					statement.execute(initSql);
				}
			} finally {
				if (statement != null) {
					statement.close();
				}
			}
		}
		return connection;
	}

	@Override
	public void close(){
		if (closed) {
			return;
		}
		synchronized (this) {
			if (!closed) {
				closed = true;
				connectionGetter.shutdown();
				connectionValidator.shutdown();
				for (DragonConnection connection : connectionPool) {
					closeRealConnection(connection);
				}
			}
		}
	}

	private void closeRealConnection(DragonConnection connection) {
		try {
			connection.getConnection().close();
		} catch (SQLException ignore) {

		}finally {
			decrementPoolSize();
			if (poolSize < minPoolSize && !closed) {
				fillConnectionPoolAsync();
			}
		}
	}

	public boolean isClosed(){
		return closed;
	}

	private synchronized void incrementPoolSize() {
		poolSize += 1;
	}

	private synchronized void decrementPoolSize() {
		poolSize--;
	}

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

	public long getCheckoutTimeout() {
		return checkoutTimeout;
	}

	public void setCheckoutTimeout(long checkoutTimeout) {
		this.checkoutTimeout = checkoutTimeout;
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

	public int getPoolSize(){
		return poolSize;
	}

	public int getIdleSize(){
		return connectionPool.size();
	}

	public int getBusySize(){
		return getPoolSize()-getIdleSize();
	}


	@Override
	public String toString() {
		return "[poolSize=" + getPoolSize() + ",busySize=" + getBusySize() + ",idleSize=" + getIdleSize()+"]";
	}
}
