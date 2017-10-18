package com.tianshouzhi.dragon.ha.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import com.tianshouzhi.dragon.ha.jdbc.datasource.DragonHADatasource;
import com.tianshouzhi.dragon.real.RealDataSourceFactory;
import com.tianshouzhi.dragon.real.jdbc.RealDataSource;
import org.junit.After;
import org.junit.Before;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by TIANSHOUZHI336 on 2016/12/8.
 */
public abstract class BaseTest {
	DragonHADatasource dragonHADatasource;

	DragonHAConnection connection;

	@Before
	public void init() throws Exception {
		dragonHADatasource = new DragonHADatasource();
		HashMap<String, RealDataSource> realDataSourceMap = new HashMap<>();
		DruidDataSource master = createRealDataSource();
		realDataSourceMap.put("master", RealDataSourceFactory.create("master", 0, 10, master));
		DruidDataSource slave1 = createRealDataSource();
		realDataSourceMap.put("slave1", RealDataSourceFactory.create("slave1", 10, 0, slave1));
		DruidDataSource slave2 = createRealDataSource();
		realDataSourceMap.put("slave2", RealDataSourceFactory.create("slave1", 10, 0, slave2));

//		dragonHADatasource.setRealDataSources(realDataSourceMap);
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
