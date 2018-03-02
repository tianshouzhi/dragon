package com.tianshouzhi.dragon.physical;

import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by tianshouzhi on 2018/2/9.
 */
public class ConnectionManagementTest {

	long count = 0;

	@Before
	public void before() {
		count = 0;
	}

		//4276154
	@Test
	public void testDruid() {
		final DruidDataSource dataSource = new DruidDataSource(false);
		dataSource.setUsername("root");
		dataSource.setPassword("");
		dataSource.setUrl("jdbc:mysql://localhost:3306/test");
		dataSource.setInitialSize(5);
		dataSource.setMaxActive(10);
		dataSource.setMinIdle(5);
		dataSource.setMaxWait(1000);
		doTest(dataSource);
	}

	//337111334
	@Test
	public void testHikariCP() {
		final HikariDataSource dataSource = new HikariDataSource();
		dataSource.setConnectionTimeout(1000);
		dataSource.setMinimumIdle(5);
		dataSource.setMaximumPoolSize(10);
		dataSource.setUsername("root");
		dataSource.setPassword("");
		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test");
		doTest(dataSource);
	}
	//199855240
	@Test
	public void testPhysical() {
		final PhysicalDataSource dataSource = new PhysicalDataSource();
		dataSource.setUsername("root");
		dataSource.setPassword("");
		dataSource.setUrl("jdbc:mysql://localhost:3306/test");
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setInitPoolSize(5);
		dataSource.setMaxPoolSize(10);
		dataSource.setMinPoolSize(5);
		dataSource.setTestWhenIdle(true);
		dataSource.setMinEvictableIdleTime(1000*60);
		dataSource.setCheckConnectionTimeout(1000);
		dataSource.init();
		doTest(dataSource);
	}

	public synchronized void count() {
		count++;
	}

	public void doTest(final DataSource dataSource) {
		for (int i = 0; i < 50; i++) {
			Thread thread = new Thread() {
				@Override
				public void run() {
					while (true) {
						Connection connection = null;
						try {
							connection = dataSource.getConnection();
						} catch (SQLException e) {
							e.printStackTrace();
						} finally {
							if (connection != null) {
								try {
									connection.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
						count();
					}

				}
			};
			thread.setDaemon(true);
			thread.start();
		}
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(count);
	}
}
