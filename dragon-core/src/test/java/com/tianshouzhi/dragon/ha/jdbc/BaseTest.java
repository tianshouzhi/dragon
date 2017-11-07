package com.tianshouzhi.dragon.ha.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import com.tianshouzhi.dragon.ha.jdbc.datasource.DragonHADatasource;
import org.junit.After;
import org.junit.Before;

import java.sql.SQLException;

/**
 * Created by TIANSHOUZHI336 on 2016/12/8.
 */
public abstract class BaseTest {
	DragonHADatasource dragonHADatasource;

	DragonHAConnection connection;

	@Before
	public void init() throws Exception {
		dragonHADatasource = new DragonHADatasource();
		DruidDataSource master = createRealDataSource();
		DruidDataSource slave1 = createRealDataSource();
		DruidDataSource slave2 = createRealDataSource();
		dragonHADatasource.addRealDatasource("master", 0, 10, master);
		dragonHADatasource.addRealDatasource("slave1", 10, 0, slave1);
		dragonHADatasource.addRealDatasource("slave2", 10, 0, slave2);
		connection = (DragonHAConnection) dragonHADatasource.getConnection();
	}

	private DruidDataSource createRealDataSource() {
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setUsername("root");
		druidDataSource.setPassword("shxx12151022");
		druidDataSource.setUrl("jdbc:mysql://localhost:3306/test");
		return druidDataSource;
	}

	@After
	public void tearDown() throws SQLException {
		if (connection != null)
			connection.close();
	}
}
