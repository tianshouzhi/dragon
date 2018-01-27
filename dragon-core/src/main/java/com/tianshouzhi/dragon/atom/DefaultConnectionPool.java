package com.tianshouzhi.dragon.atom;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.*;

/**
 * Created by tianshouzhi on 2018/1/23.
 */
public class DefaultConnectionPool {
	private String url;

	private String username;

	private String password;

	private int initPoolSize;

	private int minPoolSize;

	private int maxPoolSize;

	private int checkConnectionTimeout = 1000;

	private BlockingQueue<Connection> pool = null;

	private int timeBetweenRunEvict = 5 * 60 * 1000;

	private int connectionSize = 0;

	private ExecutorService executorService = null;

	public DefaultConnectionPool(String url, String username, String password, int initPoolSize, int minPoolSize,
								 int maxPoolSize, int checkConnectionTimeout) throws Exception {
		this.url = url;
		this.username = username;
		this.password = password;
		this.initPoolSize = initPoolSize;
		this.minPoolSize = minPoolSize;
		this.maxPoolSize = maxPoolSize;
		this.checkConnectionTimeout = checkConnectionTimeout;
		initPool();
		this.connectionSize = initPoolSize;
		initExecutor();
	}

	private void initPool() throws InterruptedException, SQLException {
		pool = new LinkedBlockingQueue<Connection>(maxPoolSize);
		if (initPoolSize > 0) {
			for (int i = 0; i < initPoolSize; i++) {
				pool.put(getConnectionFromDB());
			}
		}
	}

	private void initExecutor() {
		executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				return thread;
			}
		});
	}

	public Connection borrowConnection() throws SQLException {

		if (pool.isEmpty() && connectionSize < maxPoolSize) {
			submitGetConnectionTask();
		}

		Connection connection = null;
		try {
			connection = pool.poll(checkConnectionTimeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new SQLException("thread interrupted", e);
		}

		if (connection == null) {
			throw new SQLException("check connection timeout[" + checkConnectionTimeout + "]!");
		}

		return (Connection) Proxy.newProxyInstance(DefaultConnectionPool.class.getClassLoader(),
		      new Class[] { Connection.class }, ConnectionInvocationHandler(connection));

	}

	private void submitGetConnectionTask() {
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				synchronized (DefaultConnectionPool.this) {
					if (connectionSize >= maxPoolSize) {
						return;
					}
				}
				boolean success = false;
				Connection realConnection = null;
				try {
					realConnection = getConnectionFromDB();
					pool.put(realConnection);
					success = true;
				} catch (Throwable e) {
					if (realConnection != null) {
						try {
							realConnection.close();
						} catch (SQLException ignore) {
						}
					}
				} finally {
					if (success) {
						synchronized (DefaultConnectionPool.this) {
							connectionSize++;
						}
					}
				}
			}
		});
	}

	private InvocationHandler ConnectionInvocationHandler(final Connection connection) {
		return new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if ("close".equals(method.getName())) {
					if (connection.isClosed()) {
						synchronized (DefaultConnectionPool.this) {
							connectionSize--;
						}
						return null;
					}

					try {
						pool.put(connection);
					} catch (Exception ignore) {
					}

					return null;
				} else {
					return method.invoke(proxy, args);
				}
			}
		};
	}

	private Connection getConnectionFromDB() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}
}
