package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSourceAdapter;
import com.tianshouzhi.dragon.common.log.Log;
import com.tianshouzhi.dragon.common.log.LoggerFactory;
import com.tianshouzhi.dragon.ha.config.HAConfigManager;
import com.tianshouzhi.dragon.ha.config.HADataSourceConfig;
import com.tianshouzhi.dragon.ha.config.HALocalConfigManager;
import com.tianshouzhi.dragon.ha.config.RealDataSourceConfig;
import com.tianshouzhi.dragon.ha.exception.DataSourceMonitor;
import com.tianshouzhi.dragon.ha.exception.DragonHAException;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import com.tianshouzhi.dragon.ha.router.RouterManager;
import com.tianshouzhi.dragon.ha.util.DatasourceUtil;

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

	private String haDSName;

	private static final Log LOGGER = LoggerFactory.getLogger(DragonHADatasource.class);

	private Map<String, RealDataSourceWrapper> realDSWrapperMap = new ConcurrentHashMap<String, RealDataSourceWrapper>(4);

	private HAConfigManager configManager;

	private boolean lazyInit = true;

	private volatile RouterManager routerManager;

	public DragonHADatasource() {
		this.haDSName=DatasourceUtil.generateDataSourceName("program");
	}

	@Override
	protected void doInit() throws Exception {
		if (realDSWrapperMap.isEmpty()) {// 没有通过编程式方式设置datasource
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
			for (RealDataSourceWrapper realDataSourceWrapper : realDSWrapperMap.values()) {
				realDataSourceWrapper.init();
			}
		}
		this.routerManager = new RouterManager(this);
	}

	@Override
	protected DragonHAConnection doGetConnection(String username, String password) throws SQLException {
		return new DragonHAConnection(username, password, this);
	}

	@Override
	public void close() throws Exception {
		LOGGER.info("close dragon ha datasource start ...");
		for (RealDataSourceWrapper realDataSourceWrapper : this.realDSWrapperMap.values()) {
			LOGGER.info("close real datasource[" + realDataSourceWrapper.getRealDSName() + "]...");
			realDataSourceWrapper.close();
			LOGGER.info("close real datasource[" + realDataSourceWrapper.getRealDSName() + "] success...");
		}
		LOGGER.info("close dragon ha datasource ...");
	}

	public void addRealDatasource(String index, int readWeight, int writeWeight, DataSource dataSource) {
		realDSWrapperMap.put(index, new RealDataSourceWrapper(index, readWeight, writeWeight, dataSource));
	}

	public void addRealDatasource(String index, int readWeight, int writeWeight, Properties properties, String dsClass) {
		RealDataSourceWrapper realDataSourceWrapper = new RealDataSourceWrapper(haDSName,index, readWeight, writeWeight,
				properties, dsClass);
		realDSWrapperMap.put(index, realDataSourceWrapper);
	}

	public RouterManager getRouterManager() {
		return routerManager;
	}

	public Connection getConnectionByRealDSName(String realDSName) throws SQLException {
		RealDataSourceWrapper realDataSourceWrapper = this.realDSWrapperMap.get(realDSName);
		if (realDataSourceWrapper == null) {
			throw new DragonHAException("not valid datasource found with realDSName:" + realDSName);
		}
		if(!DataSourceMonitor.isAvailable(haDSName,realDataSourceWrapper)){
			throw new DragonHAException(realDataSourceWrapper.getRealDSName()+" is not available!!!");
		}
		try{
			return realDataSourceWrapper.getConnection();
		}catch (SQLException e){
			DataSourceMonitor.monitor(e, haDSName,realDataSourceWrapper);
			throw e;
		}
	}

	public void setLocalConfigFile(String configFile) {
		this.haDSName = DatasourceUtil.generateDataSourceName("classpath:"+configFile);
		this.configManager = new HALocalConfigManager(configFile);
	}

	public Map<String,RealDataSourceWrapper> getRealDataSourceWrapperMap(){
		return realDSWrapperMap;
	}

	public String getHADSName() {
		return haDSName;
	}
}
