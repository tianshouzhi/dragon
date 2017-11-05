package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSourceAdapter;
import com.tianshouzhi.dragon.common.log.Log;
import com.tianshouzhi.dragon.common.log.LoggerFactory;
import com.tianshouzhi.dragon.ha.config.HAConfigManager;
import com.tianshouzhi.dragon.ha.config.HADataSourceConfig;
import com.tianshouzhi.dragon.ha.config.HALocalConfigManager;
import com.tianshouzhi.dragon.ha.config.RealDataSourceConfig;
import com.tianshouzhi.dragon.ha.exception.DragonHAException;
import com.tianshouzhi.dragon.ha.exception.DragonHARuntimeException;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import com.tianshouzhi.dragon.ha.router.RouterManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class DragonHADatasource extends DragonDataSourceAdapter {
	private static final Log LOGGER = LoggerFactory.getLogger(DragonHADatasource.class);

	private Map<String, RealDataSourceWapper> dsWrappers = new ConcurrentHashMap<String, RealDataSourceWapper>(4);

	private HAConfigManager configManager;

	private boolean lazyInit = true;

	private volatile RouterManager routerManager;

	@Override
	protected void doInit() throws Exception {
		if (dsWrappers.isEmpty()) {// 没有通过编程式方式设置datasource
			if (configManager == null) {
				throw new DragonHAException("configManager can't be null !");
			} else {
				HADataSourceConfig haDataSourceConfig = configManager.getHADataSourceConfig();
				Map<String, RealDataSourceConfig> realDataSourceConfigMap = haDataSourceConfig.getRealDataSourceConfigMap();
				for (Map.Entry<String, RealDataSourceConfig> configEntry : realDataSourceConfigMap.entrySet()) {
					String key = configEntry.getKey();
					RealDataSourceConfig config = configEntry.getValue();
					addRealDatasource(key, config.getReadWeight(), config.getWriteWeight(), config.getRealDsProperties(),
					      config.getRealDsClass());
				}
			}
		}
		if (!lazyInit) {
			for (RealDataSourceWapper realDataSourceWapper : dsWrappers.values()) {
				realDataSourceWapper.init();
			}
		}
		this.routerManager = new RouterManager(this.dsWrappers);
	}

	@Override
	protected DragonHAConnection doGetConnection(String username, String password) throws SQLException {
		return new DragonHAConnection(username, password, this);
	}

	@Override
	public void close() throws Exception {
		LOGGER.info("close dragon ha datasource start ...");
		for (RealDataSourceWapper realDataSourceWrapper : this.dsWrappers.values()) {
			LOGGER.info("close real datasource[" + realDataSourceWrapper.getIndex() + "]...");
			realDataSourceWrapper.close();
			LOGGER.info("close real datasource[" + realDataSourceWrapper.getIndex() + "] success...");
		}
		LOGGER.info("close dragon ha datasource ...");
	}

	public void addRealDatasource(String index, int readWeight, int writeWeight, DataSource dataSource) {
		dsWrappers.put(index, new RealDataSourceWapper(index, readWeight, writeWeight, dataSource));
	}

	public void addRealDatasource(String index, int readWeight, int writeWeight, Properties properties, String dsClass) {
		RealDataSourceWapper realDataSourceWapper = new RealDataSourceWapper(index, readWeight, writeWeight,
				properties, dsClass);
		dsWrappers.put(index, realDataSourceWapper);
	}

	public RouterManager getRouterManager() {
		return routerManager;
	}

	public Connection getConnectionByDbIndex(String index) throws SQLException {
		RealDataSourceWapper realDataSourceWrapper = this.dsWrappers.get(index);
		if (realDataSourceWrapper == null) {
			throw new DragonHARuntimeException("not valid datasource found with index:" + index);
		}
		return realDataSourceWrapper.getConnection();
	}

	public void setLocalConfigFile(String configFile) {
		this.configManager = new HALocalConfigManager(configFile);
	}
}
