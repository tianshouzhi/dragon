package com.tianshouzhi.dragon.physical;

import com.tianshouzhi.dragon.common.thread.DragonThreadFactory;

import java.sql.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.*;

/**
 * Created by tianshouzhi on 2018/1/28.
 */
public abstract class PhysicalConnectionPool extends PhysicalDataSourceConfig implements AutoCloseable {

	protected BlockingQueue<PhysicalPooledConnection> pool = null;

	// 异步获取链接的线程池
	protected ExecutorService connGetExecutor = null;

	// 链接有效性检测连接池
	protected ScheduledExecutorService connValidChecker = null;

	protected boolean init = false;

	protected boolean closed = false;

	protected int currentTotalCount;// 当前链接总数=pool中的连接数+已经借出去的连接数

	public void init() {
		if (init) {
			return;
		}
		synchronized (this) {
			if (!init) {
				try {
					super.checkConfig();
					this.registerDriver();
					this.initPool();
					this.initExecutor();
					this.init = true;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
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

	private void initPool() throws Exception {
		pool = new LinkedBlockingQueue<PhysicalPooledConnection>(maxPoolSize);
		if (initPoolSize > 0) {
			for (int i = 0; i < initPoolSize; i++) {
				incrPoolConnection();
				synchronized (this){
					currentTotalCount++;
				}
			}
		}
	}

	private void initExecutor() {
		this.connGetExecutor = Executors
		      .newSingleThreadExecutor(new DragonThreadFactory("DRAGON-SINGLE-DATASOURCE-POOL", true));
		this.connValidChecker = Executors
		      .newSingleThreadScheduledExecutor(new DragonThreadFactory("DRAGON-SINGLE-DATASOURCE-POOL", true));
		if (testWhenIdle) {
			this.connValidChecker.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					Iterator<PhysicalPooledConnection> iterator = pool.iterator();

					while (iterator.hasNext()) {
						PhysicalPooledConnection connection = iterator.next();

						//如果达到最小驱逐时间,且当前连接数超过最小链接。驱逐、即关闭多余的链接
						if (System.currentTimeMillis() - connection.getLastActiveTime() >= minEvictableIdleTime
						      && currentTotalCount > minPoolSize) {
							pool.remove(connection);
							closePhysicalConnection(connection);
							continue;
						}
						
						try {
							connection.isValid(validationTimeout);
						} catch (SQLException e) {
							closePhysicalConnection(connection);
						}
					}
				}
			}, 10, validationInterval, TimeUnit.MILLISECONDS);
		}
	}

	private void closePhysicalConnection(PhysicalPooledConnection connection) {
		try {
            connection.getConnection().close();
            decrCurrentTotalConnCount();
        } catch (SQLException ignore) {
        }
	}



	protected PhysicalPooledConnection borrowConnection() throws SQLException {
		checkClosed();
		init();
		try {
			return doBorrowConnection(checkConnectionTimeout);
		} catch (Throwable e) {
			throw new SQLException("check connection exception!", e);
		}
	}

	private PhysicalPooledConnection doBorrowConnection(long checkConnectionTimeout)
	      throws SQLException, InterruptedException {
		long deadline = System.currentTimeMillis() + checkConnectionTimeout;
		if (pool.isEmpty() && currentTotalCount < maxPoolSize) {
			submitGetConnectionTask();
		}

		PhysicalPooledConnection connection = null;

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
				decrCurrentTotalConnCount();
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

	protected void returnConnection(PhysicalPooledConnection connection) throws SQLException {
		if (connection == null) {
			return;
		}
		try {
			if (!connection.isClosed()) {
				pool.put(connection);
			} else {
				decrCurrentTotalConnCount();
			}
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	private void submitGetConnectionTask() {
		connGetExecutor.submit(new Runnable() {
			@Override
			public void run() {
				//预增加
				synchronized (this) {
					if (currentTotalCount >= maxPoolSize) {
						return;
					}
					currentTotalCount++;
				}
				boolean success = true;

				try {
					//增加池子中链接
					incrPoolConnection();
				} catch (Throwable e) {
					success = false;
				} finally {
					if (!success) {//如果增加失败，数量再减去1
						decrCurrentTotalConnCount();
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
		pool.put(new PhysicalPooledConnection(connection, PhysicalConnectionPool.this));
	}

	private void decrCurrentTotalConnCount() {
		synchronized (this) {
			currentTotalCount--;
			if (currentTotalCount < minPoolSize) {
				submitGetConnectionTask();
			}
		}
	}

	private void checkClosed() throws SQLException {
		if (closed) {
			throw new PhysicalSQLException("PhysicalDataSource is closed,can't get new connection!!!");
		}
	}

	@Override
	public void close() throws SQLException {
		if (closed) {
			return;
		}
		synchronized (this) {
			if (!closed) {
				shutDown();
				closed = true;
			}
		}
	}

	public synchronized void shutDown() throws SQLException {
		connGetExecutor.shutdown();
		connValidChecker.shutdown();
		for (PhysicalPooledConnection connection : pool) {
			connection.close();
		}
	}

	public int getBusyCount(){
		return currentTotalCount -pool.size();
	}

	public int getIdleCount(){
		return pool.size();
	}

	public int getTotalCount(){
		return currentTotalCount;
	}

}
