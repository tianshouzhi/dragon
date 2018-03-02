package com.tianshouzhi.dragon.physical;

import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * http://blog.csdn.net/qq_31125793/article/details/51241943
 * Created by tianshouzhi on 2018/2/9.
 */
public class StatementManagementTest {

	long count = 0;

	@Before
	public void before() {
		count = 0;
	}

	// 4276154
	@Test
	public void testDruid() throws SQLException {
		final DruidDataSource dataSource = new DruidDataSource(false);
		dataSource.setUsername("root");
		dataSource.setPassword("shxx12151022");
		dataSource.setUrl("jdbc:mysql://localhost:3306/test");
		dataSource.setInitialSize(5);
		dataSource.setMaxActive(10);
		dataSource.setMinIdle(5);
		dataSource.setMaxWait(1000);
//		dataSource.setPoolPreparedStatements(true);
//		dataSource.setMaxPoolPreparedStatementPerConnectionSize(10);
        dataSource.init();
		doTest(dataSource);
	}

	// 337111334
	// http://brettwooldridge.github.io/HikariCP/
	@Test
	public void testHikariCP() throws SQLException {
		final HikariDataSource dataSource = new HikariDataSource();
		dataSource.setConnectionTimeout(1000);
		dataSource.setMinimumIdle(5);
		dataSource.setMaximumPoolSize(10);
		dataSource.setUsername("root");
		dataSource.setPassword("shxx12151022");
		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test");
		doTest(dataSource);
	}

	// 199855240
	@Test
	public void testPhysical() throws SQLException {
		final PhysicalDataSource dataSource = new PhysicalDataSource();
		dataSource.setUsername("root");
		dataSource.setPassword("shxx12151022");
		dataSource.setUrl("jdbc:mysql://localhost:3306/test");
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setInitPoolSize(5);
		dataSource.setMaxPoolSize(10);
		dataSource.setMinPoolSize(5);
		dataSource.setTestWhenIdle(true);
		dataSource.setMinEvictableIdleTime(1000 * 60);
		dataSource.setCheckConnectionTimeout(1000);
		dataSource.init();
		doTest(dataSource);
	}

	public synchronized void count() {
		count++;
	}

	public void doTest(final DataSource dataSource) throws SQLException {
		final Connection connection = dataSource.getConnection();

        Thread thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    Statement statement = null;
                    try {
                        statement = connection.createStatement();
                        statement.execute("select * from user");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        if (statement != null) {
                            try {
                                statement.close();
                            } catch (SQLException e) {
								System.out.println(count);
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
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		connection.close();
		System.out.println(count);
	}
}
