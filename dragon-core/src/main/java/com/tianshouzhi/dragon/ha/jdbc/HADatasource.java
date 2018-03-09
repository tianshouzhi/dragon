package com.tianshouzhi.dragon.ha.jdbc;

import com.tianshouzhi.dragon.common.exception.DragonException;
import com.tianshouzhi.dragon.common.jdbc.datasource.DataSourceAdapter;
import com.tianshouzhi.dragon.common.log.Log;
import com.tianshouzhi.dragon.common.log.LoggerFactory;
import com.tianshouzhi.dragon.common.util.StringUtils;
import com.tianshouzhi.dragon.ha.config.HAConfigManager;
import com.tianshouzhi.dragon.ha.config.HADataSourceConfig;
import com.tianshouzhi.dragon.ha.config.HALocalConfigManager;
import com.tianshouzhi.dragon.ha.config.RealDsWrapperConfig;
import com.tianshouzhi.dragon.ha.exception.DataSourceMonitor;
import com.tianshouzhi.dragon.ha.exception.HAException;
import com.tianshouzhi.dragon.ha.router.HARouterManager;
import com.tianshouzhi.dragon.ha.util.DatasourceSpiUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class HADatasource extends DataSourceAdapter {

	private static final Log LOGGER = LoggerFactory.getLogger(HADatasource.class);

	private boolean lazyInit = true;

	private String configFile;

    protected String haDSName;

	private Map<String, RealDsWrapper> realDSWrapperMap = new ConcurrentHashMap<String, RealDsWrapper>(4);

	private HAConfigManager configManager;

	private HARouterManager routerManager;

	@Override
	protected void doInit() throws Exception {
		initDsName();
		LOGGER.info("init HADatasource(" + haDSName + ")");
		initConfigManager();
		initRealDSMap();
		checkLazyInit();
		initRouterManager();
	}

	private void initDsName() {
		if (StringUtils.isNotBlank(haDSName)) {
			return;
		} else {
			this.haDSName = DatasourceSpiUtil.generateDataSourceName("dragon-ha");
		}
	}

	private void initConfigManager() {
		if (StringUtils.isNotBlank(configFile)) {
			this.configManager = new HALocalConfigManager(configFile);
		}
	}

	private void initRealDSMap() {
		if (realDSWrapperMap.isEmpty()) {// 没有通过编程式方式设置datasource
			if (configManager == null) {
				throw new HAException("configManager can't be null !");
			} else {
				HADataSourceConfig haDataSourceConfig = configManager.getHADataSourceConfig();
				Map<String, RealDsWrapperConfig> realDataSourceConfigMap = haDataSourceConfig.getRealDataSourceConfigMap();
				for (Map.Entry<String, RealDsWrapperConfig> configEntry : realDataSourceConfigMap.entrySet()) {
					String realDsName = configEntry.getKey();
					RealDsWrapperConfig config = configEntry.getValue();
					RealDsWrapper realDataSourceWrapper = new RealDsWrapper(config, haDSName);
					realDSWrapperMap.put(realDsName, realDataSourceWrapper);
				}
			}
		}
	}

	private void checkLazyInit() {
		// check lazyInit
		if (!lazyInit) {
			for (RealDsWrapper realDataSourceWrapper : realDSWrapperMap.values()) {
				try {
					realDataSourceWrapper.init();
				} catch (Exception e) {
					throw new HAException("init real datasource" + realDataSourceWrapper.getRealDSName() + "error", e);
				}
			}
		}
	}

	private void initRouterManager() {
		this.routerManager = new HARouterManager(this);
	}

	@Override
	protected HAConnection doGetConnection(String username, String password) throws SQLException {
		return new HAConnection(username, password, this);
	}

	public Connection getConnectionByRealDSName(String realDSName) throws SQLException {
		RealDsWrapper realDataSourceWrapper = this.realDSWrapperMap.get(realDSName);
		if (realDataSourceWrapper == null) {
			throw new HAException("not valid datasource found with realDSName:" + realDSName);
		}
		if (!DataSourceMonitor.isAvailable(realDataSourceWrapper)) {
			throw new HAException(realDataSourceWrapper.getRealDSName() + " is not available!!!");
		}
		try {
			return realDataSourceWrapper.getConnection();
		} catch (SQLException e) {
			DataSourceMonitor.monitor(e, realDataSourceWrapper);
			throw e;
		}
	}

	@Override
	public void close() throws DragonException {
		LOGGER.info(" close HADatasource(" + haDSName + ")");
		for (RealDsWrapper realDataSourceWrapper : this.realDSWrapperMap.values()) {
			realDataSourceWrapper.close();
		}
	}

	public void setLocalConfigPath(String localConfigPath) {
		this.configFile = localConfigPath;
	}

	public Map<String, RealDsWrapper> getRealDSWrapperMap() {
		return realDSWrapperMap;
	}

	public boolean isLazyInit() {
		return lazyInit;
	}

	public void setLazyInit(boolean lazyInit) {
		this.lazyInit = lazyInit;
	}

	public HARouterManager getRouterManager() {
		return routerManager;
	}

    public String getHaDSName() {
        return haDSName;
    }

    public void setHaDSName(String haDSName) {
        this.haDSName = haDSName;
    }

}
