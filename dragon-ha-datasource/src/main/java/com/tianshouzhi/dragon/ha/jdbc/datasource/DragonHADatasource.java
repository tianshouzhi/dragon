package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.initailzer.DataSourceUtil;
import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSource;
import com.tianshouzhi.dragon.common.log.Log;
import com.tianshouzhi.dragon.common.log.LoggerFactory;
import com.tianshouzhi.dragon.ha.config.DragonHAConfiguration;
import com.tianshouzhi.dragon.ha.config.RealDatasourceConfig;
import com.tianshouzhi.dragon.ha.config.manager.DragonHAConfigurationManager;
import com.tianshouzhi.dragon.ha.config.manager.DragonHALocalConfigurationManager;
import com.tianshouzhi.dragon.ha.config.parser.DragonHAXmlConfigParser;
import com.tianshouzhi.dragon.ha.exception.DragonHAConfigException;
import com.tianshouzhi.dragon.ha.exception.DragonHAException;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class DragonHADatasource extends DragonDataSource {
	private static final Log LOGGER = LoggerFactory.getLogger(DragonHADatasource.class);

	private RealDataSourceWrapperManager realDataSourceManager;

	private DragonHAConfigurationManager configurationManager;

	private DragonHAConfiguration configuration;

	public DragonHADatasource(String configFile) throws DragonHAException {
		this(new DragonHALocalConfigurationManager(configFile));
	}

	public DragonHADatasource(DragonHAConfigurationManager configurationManager) throws DragonHAException {
		this(configurationManager.getConfiguration());
		configurationManager.setDragonHADataSource(this);
		this.configurationManager = configurationManager;
	}

	public DragonHADatasource(DragonHAConfiguration configuration) throws DragonHAException {
		checkDragonHAConfiguration(configuration);
		HashMap<String, RealDatasourceWrapper> datasourceWrapperMap = getDatasourceWrapperMap(configuration);
		if (!configuration.isLazyInit()) {
			for (RealDatasourceWrapper wrapper : datasourceWrapperMap.values()) {
				wrapper.init();
			}
		}
		this.realDataSourceManager = new RealDataSourceWrapperManager(datasourceWrapperMap);
		this.configuration = configuration;

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					close();
				} catch (SQLException ignore) {
				}
			}
		});
	}

	private void checkDragonHAConfiguration(DragonHAConfiguration configuration) throws DragonHAConfigException {
		if (configuration == null) {
			throw new DragonHAConfigException("configuration can't be null");
		}

		String appName = configuration.getAppName();
		if (StringUtils.isBlank(appName)) {
			throw new DragonHAConfigException("appName can't be blank");
		}

		List<RealDatasourceConfig> configList = configuration.getRealDataSourceConfigList();
		if (CollectionUtils.isEmpty(configList)) {
			throw new DragonHAConfigException("no real datasource config!!!");
		}

		for (RealDatasourceConfig config : configList) {
			checkRealDatasourceConfig(config);
		}
	}

	private void checkRealDatasourceConfig(RealDatasourceConfig config) throws DragonHAConfigException {
		if (config == null) {
			throw new NullPointerException();
		}

		String index = config.getIndex();

		if (StringUtils.isBlank(index)) {
			throw new DragonHAConfigException("parameter 'dataSourceIndex' can't be empty or blank");
		}

		Integer readWeight = config.getReadWeight();
		Integer writeWeight = config.getWriteWeight();

		if (readWeight < 0 || writeWeight < 0) {
			throw new DragonHAConfigException(
					"'" + index + "' config error, both 'readWeight' and 'writeWeight' can't less than zero,"
							+ "current readWeight:" + readWeight + ",current writeWeight:" + writeWeight);
		}

		try {
			DataSourceUtil.checkConnection(config.getRealClass(), config.getPropertiesMap());
		} catch (SQLException e) {
			throw new DragonHAConfigException("'" + index + "'check connection error ,please check config【" + config + "】",
					e);
		}
	}

	private HashMap<String, RealDatasourceWrapper> getDatasourceWrapperMap(DragonHAConfiguration configuration)
			throws DragonHAException {
		HashMap<String, RealDatasourceWrapper> datasourceWrapperMap = new HashMap<String, RealDatasourceWrapper>();
		for (RealDatasourceConfig realDatasourceConfig : configuration.getRealDataSourceConfigList()) {
			String index = realDatasourceConfig.getIndex();
			RealDatasourceWrapper wrapper = new RealDatasourceWrapper(realDatasourceConfig);
			datasourceWrapperMap.put(index, wrapper);
		}

		return datasourceWrapperMap;
	}

	public synchronized void refreshConfig(DragonHAConfiguration newConfiguration) throws DragonHAException {
		if (newConfiguration == null || configuration.equals(newConfiguration)) {
			return;
		} else {
			logRefresh(newConfiguration);
		}

		if (!this.configuration.getRealDataSourceConfigList().equals(newConfiguration.getRealDataSourceConfigList())) {
			try {
				Map<String, RealDatasourceWrapper> originIndexDSMap = this.realDataSourceManager.getIndexDSMap();
				HashMap<String, RealDatasourceWrapper> newIndexDSMap = getDatasourceWrapperMap(newConfiguration);

				//calculate change info
				Map<String, RealDatasourceWrapper> needToAddMap = new HashMap<String, RealDatasourceWrapper>(4);
				Map<String, RealDatasourceWrapper> needToReplaceMap = new HashMap<String, RealDatasourceWrapper>(4);
				HashMap<String, RealDatasourceWrapper> needToRemoveMap = new HashMap<String, RealDatasourceWrapper>(4);

				for (Map.Entry<String, RealDatasourceWrapper> entry : newIndexDSMap.entrySet()) {
					String newDataSourceIndex = entry.getKey();
					RealDatasourceWrapper newDataSourceWrapper = entry.getValue();
					if (originIndexDSMap.containsKey(newDataSourceIndex)) {
						if (!newDataSourceWrapper.getConfig().equals(originIndexDSMap.get(newDataSourceIndex).getConfig())) {
							needToReplaceMap.put(newDataSourceIndex, newDataSourceWrapper);
						}
					} else {
						needToAddMap.put(newDataSourceIndex, newDataSourceWrapper);
					}
				}

				for (Map.Entry<String, RealDatasourceWrapper> entry : originIndexDSMap.entrySet()) {
					String oldDataSourceIndex = entry.getKey();
					RealDatasourceWrapper oldDataSourceWrapper = entry.getValue();
					if (!newIndexDSMap.containsKey(oldDataSourceIndex)) {
						needToRemoveMap.put(oldDataSourceIndex, oldDataSourceWrapper);
					}
				}

				// check needToAddMap and needToReplaceMap,there is no need to check needToRemoveMap,just clearThreadLocalHint it
				for (RealDatasourceWrapper realDatasourceWrapper : needToAddMap.values()) {
					checkRealDatasourceConfig(realDatasourceWrapper.getConfig());
				}

				for (RealDatasourceWrapper realDatasourceWrapper : needToReplaceMap.values()) {
					checkRealDatasourceConfig(realDatasourceWrapper.getConfig());
				}

				//init after check passed
				if(!newConfiguration.isLazyInit()){
					for (RealDatasourceWrapper realDatasourceWrapper : needToAddMap.values()) {
						realDatasourceWrapper.init();
					}
					for (RealDatasourceWrapper realDatasourceWrapper : needToReplaceMap.values()) {
						realDatasourceWrapper.init();
					}
				}

				//refresh,it will be very fast (all prepare work,eg :calculate change and init, has been done,just
				// need to reset the map)
				realDataSourceManager.refresh(needToAddMap, needToReplaceMap, needToRemoveMap.keySet());

				this.configuration = newConfiguration;
			} catch (Throwable e) {
				throw new DragonHAConfigException("refresh ha config error,will keep the origin config", e);
			}
		} else {
			this.configuration = newConfiguration;
		}
	}

	@Override
	public DragonHAConnection getConnection(String username, String password) throws SQLException {
		return new DragonHAConnection(username, password, realDataSourceManager);
	}

	@Override
	public void close() throws SQLException {
		LOGGER.info("close dragon ha datasource");
		for (RealDatasourceWrapper realDatasourceWrapper : realDataSourceManager.getIndexDSMap().values()) {
			try {
				realDatasourceWrapper.close();
			} catch (Throwable ignore) {
			}
		}
	}

	private void logRefresh(DragonHAConfiguration newConfiguration) throws DragonHAConfigException {
		String msg = "try to refresh ha config .\n";
		msg += "======================origin ha config==================.\n";
		msg += DragonHAXmlConfigParser.toXml(this.configuration);
		msg += "======================new ha config==================.\n";
		msg += DragonHAXmlConfigParser.toXml(newConfiguration);
		LOGGER.info(msg);
	}
}
