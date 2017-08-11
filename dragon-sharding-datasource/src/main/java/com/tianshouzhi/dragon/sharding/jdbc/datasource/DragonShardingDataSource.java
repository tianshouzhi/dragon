package com.tianshouzhi.dragon.sharding.jdbc.datasource;

import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSource;
import com.tianshouzhi.dragon.sharding.jdbc.connection.DragonShardingConnection;
import com.tianshouzhi.dragon.sharding.route.LogicDatasource;
import com.tianshouzhi.dragon.sharding.route.LogicTable;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public class DragonShardingDataSource extends DragonDataSource {
	// 原始配置信息
	private Properties configProperties;

	private DragonShardingConfig dragonShardingConfig;

	public DragonShardingDataSource(String configFileClassPath) throws Exception {
		if (StringUtils.isBlank(configFileClassPath)) {
			throw new IllegalArgumentException("configFileClassPath can't be blank");
		}
		InputStream inputStream = ClassLoader.getSystemClassLoader().getResource(configFileClassPath).openStream();
		configProperties = new Properties();
		configProperties.load(inputStream);
		String appName = DragonShardingConfigParser.parseAppName(configProperties);

		LogicDatasource logicDatasource = DragonShardingConfigParser.parseLogicDatasouce(configProperties);
		Map<String, LogicTable> logicTableMap = DragonShardingConfigParser.parseLogicTableMap(logicDatasource,
		      configProperties);

		ExecutorService executor = DragonShardingConfigParser.makeExecutorService(appName, logicDatasource, logicTableMap,
		      configProperties);
		int executionTimeout = DragonShardingConfigParser.parseExecutionTimeout(configProperties);
		this.dragonShardingConfig = new DragonShardingConfig(appName, logicDatasource, logicTableMap, executor,
		      executionTimeout);
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return new DragonShardingConnection(username, password, dragonShardingConfig);
	}

	@Override
	public void close() throws SQLException {
		//TODO
	}
	/*
	 * public DragonShardingConfig getDragonShardingConfig() { return dragonShardingConfig; }
	 */
}
