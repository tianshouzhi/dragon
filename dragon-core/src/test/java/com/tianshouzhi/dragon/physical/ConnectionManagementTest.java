package com.tianshouzhi.dragon.physical;

import com.alibaba.druid.pool.DruidDataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.PrintWriter;
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

	// 4276154 122
	@Test
	public void testDruid() throws SQLException {
		final DruidDataSource dataSource = new DruidDataSource(false);
		dataSource.setUsername("root");
		dataSource.setPassword("xx");
		dataSource.setUrl("jdbc:mysql://localhost:3306/test?useSSL=false");
		dataSource.setInitialSize(5);
		dataSource.setMaxActive(10);
		dataSource.setMinIdle(5);
		dataSource.setMaxWait(1000);
		dataSource.init();
		doTest(dataSource);
	}

	// 40903762 173
	@Test
	public void testDbcp2() throws SQLException, InterruptedException {
		BasicDataSource basicDataSource = new BasicDataSource();
		basicDataSource.setUrl("jdbc:mysql://localhost:3306/test?useSSL=false");
		basicDataSource.setUsername("root");
		basicDataSource.setPassword("shxx12151022");
		basicDataSource.setLogWriter(new PrintWriter(System.out));
		basicDataSource.setMaxTotal(10);
		basicDataSource.setMinIdle(5);
		basicDataSource.setMaxWaitMillis(1000);
		basicDataSource.setAccessToUnderlyingConnectionAllowed(true);
		basicDataSource.setTestOnBorrow(false);
		basicDataSource.setTestOnReturn(false);

		basicDataSource.getNumActive();
		basicDataSource.getNumIdle();
		basicDataSource.setTimeBetweenEvictionRunsMillis(1000);
//
//
//		Connection connection = basicDataSource.getConnection();
		basicDataSource.close();
		Thread.sleep(1000000000);

//		//
//		doTest(basicDataSource);
	}

	@Test
	public void testTomcatJdbc() throws SQLException {
		org.apache.tomcat.jdbc.pool.DataSource datasource = new org.apache.tomcat.jdbc.pool.DataSource();
		datasource.setUrl("jdbc:mysql://localhost:3306/test?useSSL=false");
		datasource.setUsername("root");
//		datasource.setDriverClassName("com.mysql.jdbc.Driver");
		datasource.setPassword("shxx12151022");
		datasource.setInitialSize(10);
		datasource.setMaxActive(50);
		datasource.setLogWriter(new PrintWriter(System.out));
		datasource.setMinIdle(10);
		datasource.setLoginTimeout(1);
		Connection connection = datasource.getConnection();
		System.out.println(datasource.getPoolSize());
		System.out.println(datasource.getIdle());
		System.out.println(datasource.getActive());
	}

	// 2299153 220%
	@Test
	public void testC3p0() throws PropertyVetoException {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setUser("root");
		dataSource.setPassword("shxx12151022");
		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test?useSSL=false");
		dataSource.setDriverClass("com.mysql.jdbc.Driver");
		dataSource.setMaxPoolSize(10);
		dataSource.setMaxPoolSize(5);
		dataSource.setCheckoutTimeout(1000);
		doTest(dataSource);
	}

	// 337111334 423
	// http://brettwooldridge.github.io/HikariCP/
	@Test
	public void testHikariCP() {
		final HikariDataSource dataSource = new HikariDataSource();
		dataSource.setConnectionTimeout(1000);
		dataSource.setMinimumIdle(5);
		dataSource.setMaximumPoolSize(10);
		dataSource.setUsername("dragon_ha");
		dataSource.setPassword("dragon_ha");
		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test?useSSL=false");
		doTest(dataSource);
	}

	// 199855240
	@Test
	public void testPhysical() throws SQLException {
		final DragonDataSource dataSource = new DragonDataSource();
		dataSource.setUsername("dragon_ha");
		dataSource.setPassword("dragon_ha");
		dataSource.setUrl("jdbc:mysql://localhost:3306/test?useSSL=false");
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setInitPoolSize(5);
		dataSource.setMaxPoolSize(10);
		dataSource.setMinPoolSize(5);
		dataSource.setTestWhenIdle(true);
		dataSource.setMinEvictableIdleTime(1000 * 60);
		dataSource.setCheckoutTimeout(1000);
		dataSource.init();
		doTest(dataSource);
	}

	public synchronized void count() {
		count++;
	}

	public void doTest(final DataSource dataSource) {
		for (int i = 0; i < 6; i++) {
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
