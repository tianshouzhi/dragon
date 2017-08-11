package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.exception.DragonHAConfigException;
import com.tianshouzhi.dragon.common.initailzer.DataSourceUtil;
import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSource;
import com.tianshouzhi.dragon.ha.config.RealDatasourceConfig;
import com.tianshouzhi.dragon.ha.config.DragonHAConfiguration;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class DragonHADatasource extends DragonDataSource {
	private static final Logger LOGGER = LoggerFactory.getLogger(DragonHADatasource.class);

	private RealDataSourceWrapperManager realDataSourceWrapperManager;

	private DragonHAConfiguration configuration;

	public DragonHADatasource(DragonHAConfiguration configuration) throws Exception {
		init(configuration);
		this.configuration = configuration;
	}

	private void init(DragonHAConfiguration configuration) throws SQLException {
		checkDragonHAConfiguration(configuration);
		HashMap<String, RealDatasourceWrapper> indexDSMap = new HashMap<String, RealDatasourceWrapper>();
		for (RealDatasourceConfig realDatasourceConfig : configuration.getRealDataSourceConfigList()) {
			String index = realDatasourceConfig.getIndex();
			RealDatasourceWrapper wrapper = new RealDatasourceWrapper(realDatasourceConfig);
			indexDSMap.put(index, wrapper);
		}

		if (!configuration.isLazyInit()) {
			for (RealDatasourceWrapper wrapper : indexDSMap.values()) {
				wrapper.init();
			}
		}

		this.realDataSourceWrapperManager = new RealDataSourceWrapperManager();
		this.realDataSourceWrapperManager.refresh(indexDSMap);
	}

	private void checkDragonHAConfiguration(DragonHAConfiguration configuration) throws DragonHAConfigException {

		if(configuration==null){
			throw new DragonHAConfigException("configuration can't be null");
		}

		String appName = configuration.getAppName();
		if(StringUtils.isBlank(appName)){
			throw new DragonHAConfigException("appName can't be blank");
		}

		List<RealDatasourceConfig> configList = configuration.getRealDataSourceConfigList();
		if(CollectionUtils.isEmpty(configList)){
			throw new DragonHAConfigException("no real datasource config!!!");
		}

		for (RealDatasourceConfig config : configList) {
			checkRealDatasourceConfig(config);
		}

	}

	private void checkRealDatasourceConfig(RealDatasourceConfig config) {
		if (config == null) {
			throw new NullPointerException();
		}

		String index = config.getIndex();

		if (StringUtils.isBlank(index)) {
			throw new IllegalArgumentException("parameter 'dataSourceIndex' can't be empty or blank");
		}

		Integer readWeight = config.getReadWeight();
		Integer writeWeight = config.getWriteWeight();

		if (readWeight < 0 || writeWeight < 0 ) {
			throw new IllegalArgumentException(
					"'"+ index + "' config error, both 'readWeight' and 'writeWeight' can't less than zero," +
							"current readWeight:"+ readWeight + ",current writeWeight:" + writeWeight);
		}

		try {
			DataSourceUtil.checkConnection(config.getRealClass(), config.getPropertiesMap());
		} catch (SQLException e) {
			throw new IllegalArgumentException("config error ,please checkRealDatasourceConfig【"+config+"】",e);
		}
	}

	@Override
	public DragonHAConnection getConnection(String username, String password) throws SQLException {
		return new DragonHAConnection(username, password, realDataSourceWrapperManager);
	}

	private synchronized void refreshConfig(DragonHAConfiguration newConfiguration) throws SQLException {
		// init(newConfiguration);
	}

	@Override
	public void close() throws SQLException {

		for (RealDatasourceWrapper realDatasourceWrapper : realDataSourceWrapperManager.getIndexDSMap().values()) {
			try {
				DataSourceUtil.close(realDatasourceWrapper.getRealDataSource());
			} catch (Exception e) {
				LOGGER.error("close datasource '" + realDatasourceWrapper + "' error", e);
			}
		}
	}
}
