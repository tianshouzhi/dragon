package com.tianshouzhi.dragon.single;

import com.tianshouzhi.dragon.common.thread.DragonThreadFactory;
import com.tianshouzhi.dragon.common.util.StringUtils;

import java.sql.*;
import java.util.Iterator;
import java.util.concurrent.*;

/**
 * Created by tianshouzhi on 2018/1/28.
 */
public abstract class AbstractDataSourcePool extends DataSourceConfig implements AutoCloseable {

	private BlockingQueue<SinglePooledConnection> pool = null;

	private ExecutorService connGetExecutor = null;

	private ScheduledExecutorService connValidChecker = null;

	private boolean init = false;

	private int totalConnection;// 当前链接总数

	public void init() {
		if (init) {
			return;
		}
		synchronized (this) {
			if (!init) {
				try {
					checkConfig();
					registerDriver();
					initPool();
					initExecutor();
					this.init = true;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private void registerDriver() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		DriverManager.registerDriver((Driver) Class.forName(driverClassName).newInstance());
	}

	private void checkConfig() {
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

	private void initExecutor() {
		this.connGetExecutor = Executors
		      .newSingleThreadExecutor(new DragonThreadFactory("DRAGON-SINGLE-DATASOURCE-POOL", true));
		this.connValidChecker = Executors
		      .newSingleThreadScheduledExecutor(new DragonThreadFactory("DRAGON-SINGLE-DATASOURCE-POOL", true));
		if(testWhenIdle){
			this.connValidChecker.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					Iterator<SinglePooledConnection> iterator = pool.iterator();
					while (iterator.hasNext()) {
						SinglePooledConnection connection = iterator.next();
						try {
							connection.isValid(validationTimeout);
							connection.updateLastActiveTime();
						} catch (SQLException e) {
							decrPoolConnection();
							connection = null;
						}
					}
				}
			}, 10, validationInterval, TimeUnit.MILLISECONDS);
		}
	}

	private void initPool() throws InterruptedException, SQLException {
		pool = new LinkedBlockingQueue<SinglePooledConnection>(maxPoolSize);
		if (initPoolSize > 0) {
			for (int i = 0; i < initPoolSize; i++) {
				incrPoolConnection();
				totalConnection++;
			}
		}
	}

	protected SinglePooledConnection borrowConnection() throws SQLException {
		init();
		try {
			return doBorrowConnection(checkConnectionTimeout);
		} catch (Throwable e) {
			throw new SQLException("check connection exception!", e);
		}
	}

	private SinglePooledConnection doBorrowConnection(long checkConnectionTimeout) throws SQLException, InterruptedException {
		long deadline = System.currentTimeMillis() + checkConnectionTimeout;
		if (pool.isEmpty() && totalConnection < maxPoolSize) {
			submitGetConnectionTask();
		}

		SinglePooledConnection connection = null;

		if (checkConnectionTimeout > 0) {
			connection = pool.poll(checkConnectionTimeout, TimeUnit.MILLISECONDS);
			if (connection == null) {
				throw new SQLException("check connection timeout[" + checkConnectionTimeout + "]!");
			}
		} else {
			connection = pool.take();
		}

		if (testOnBorrow) {
			boolean valid = false;
			try {
				valid = connection.isValid(validationTimeout);
			} catch (Throwable ignore) {
				decrPoolConnection();
			}

			if (valid) {
				return connection;
			}

			if (checkConnectionTimeout == 0) {
				connection = doBorrowConnection(0);
			} else {
				long remainderTimeout = deadline - System.currentTimeMillis();
				if (remainderTimeout <= 0) {
					throw new SQLException("check connection timeout[" + checkConnectionTimeout + "]!");
				}
				connection = doBorrowConnection(remainderTimeout);
			}

		}
		return connection;
	}

	protected void returnConnection(SinglePooledConnection connection) throws SQLException {
		if (connection == null) {
			return;
		}
		try {
			if (!connection.isClosed()) {
				pool.put(connection);
			} else {
				decrPoolConnection();
			}
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	private void submitGetConnectionTask() {
		connGetExecutor.submit(new Runnable() {
			@Override
			public void run() {
				synchronized (this) {
					if (totalConnection >= maxPoolSize) {
						return;
					}
					totalConnection++;
				}
				boolean success = true;

				try {
					incrPoolConnection();
				} catch (Throwable e) {
					success = false;
				} finally {
					if (!success) {
						decrPoolConnection();
					}
				}
			}
		});
	}

	private void incrPoolConnection() throws SQLException, InterruptedException {
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

		// add to the pool
//		pool.put(new SinglePooledConnection(connection, AbstractDataSourcePool.this));
	}

	private void decrPoolConnection() {
		synchronized (this) {
			totalConnection--;
			if (totalConnection < minPoolSize) {
				submitGetConnectionTask();
			}
		}

	}

	@Override
	public void close() throws Exception {

	}
}
