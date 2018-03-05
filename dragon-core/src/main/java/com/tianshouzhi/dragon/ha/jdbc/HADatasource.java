package com.tianshouzhi.dragon.ha.jdbc;

import com.tianshouzhi.dragon.common.exception.DragonException;
import com.tianshouzhi.dragon.common.jdbc.datasource.DataSourceAdapter;
import com.tianshouzhi.dragon.common.log.Log;
import com.tianshouzhi.dragon.common.log.LoggerFactory;
import com.tianshouzhi.dragon.common.util.StringUtils;
import com.tianshouzhi.dragon.ha.config.HAConfigManager;
import com.tianshouzhi.dragon.ha.config.HADataSourceConfig;
import com.tianshouzhi.dragon.ha.config.HALocalConfigManager;
import com.tianshouzhi.dragon.ha.config.RealDataSourceConfig;
import com.tianshouzhi.dragon.ha.exception.DataSourceMonitor;
import com.tianshouzhi.dragon.ha.exception.DragonHAException;
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
public class HADatasource extends DataSourceAdapter {

    private static final Log LOGGER = LoggerFactory.getLogger(HADatasource.class);

    private boolean lazyInit = true;

    private String configFile;

    private Map<String, RealDataSourceWrapper> realDSMap = new ConcurrentHashMap<String, RealDataSourceWrapper>(4);

    private HAConfigManager configManager;

    private RouterManager routerManager;

    @Override
    protected void doInit() throws Exception {
        initDsName();
        LOGGER.info("init HADatasource(" + dsName + ")");
        initConfigManager();
        initRealDSMap();
        initRouterManager();
    }

    private void initDsName() {
        if (StringUtils.isNotBlank(dsName)) {
            return;
        }else{
            this.dsName = DatasourceUtil.generateDataSourceName("dragon-ha");
        }
    }

    private void initConfigManager() {
        if (StringUtils.isNotBlank(configFile)) {
            this.configManager = new HALocalConfigManager(configFile);
        }
    }

    private void initRealDSMap() {
        if (realDSMap.isEmpty()) {// 没有通过编程式方式设置datasource
            if (configManager == null) {
                throw new DragonHAException("configManager can't be null !");
            } else {
                HADataSourceConfig haDataSourceConfig = configManager.getHADataSourceConfig();
                Map<String, RealDataSourceConfig> realDataSourceConfigMap = haDataSourceConfig.getRealDataSourceConfigMap();
                for (Map.Entry<String, RealDataSourceConfig> configEntry : realDataSourceConfigMap.entrySet()) {
                    String realDsName = configEntry.getKey();
                    RealDataSourceConfig config = configEntry.getValue();
                    addRealDatasource(this.dsName,realDsName, config.getReadWeight(), config.getWriteWeight(), config.getRealDsProperties(), config.getRealDsClass());
                }
            }
        }

        //check lazyInit
        if (!lazyInit) {
            for (RealDataSourceWrapper realDataSourceWrapper : realDSMap.values()) {
                try {
                    realDataSourceWrapper.init();
                } catch (Exception e) {
                    throw new DragonHAException("init real datasource" + realDataSourceWrapper.getFullName() + "error", e);
                }
            }
        }
    }

    private void initRouterManager() {
        this.routerManager = new RouterManager(this);
    }

    public void addRealDatasource(String index, int readWeight, int writeWeight, DataSource dataSource) {
        realDSMap.put(index, new RealDataSourceWrapper(index, readWeight, writeWeight, dataSource));
    }

    public void addRealDatasource(String haDSName,String realDSName, int readWeight, int writeWeight, Properties properties, String dsClass) {
        RealDataSourceWrapper realDataSourceWrapper = new RealDataSourceWrapper(haDSName, realDSName, readWeight, writeWeight,
                properties, dsClass);
        realDSMap.put(realDSName, realDataSourceWrapper);
    }

    @Override
    protected DragonHAConnection doGetConnection(String username, String password) throws SQLException {
        return new DragonHAConnection(username, password, this);
    }

    public Connection getConnectionByRealDSName(String realDSName) throws SQLException {
        RealDataSourceWrapper realDataSourceWrapper = this.realDSMap.get(realDSName);
        if (realDataSourceWrapper == null) {
            throw new DragonHAException("not valid datasource found with realDSName:" + realDSName);
        }
        if (!DataSourceMonitor.isAvailable(realDataSourceWrapper)) {
            throw new DragonHAException(realDataSourceWrapper.getFullName() + " is not available!!!");
        }
        try {
            return realDataSourceWrapper.getConnection();
        } catch (SQLException e) {
            DataSourceMonitor.monitor(e,realDataSourceWrapper);
            throw e;
        }
    }

    @Override
    public void close() throws DragonException {
        LOGGER.info(" close HADatasource(" + getDsName() + ")");
        for (RealDataSourceWrapper realDataSourceWrapper : this.realDSMap.values()) {
            realDataSourceWrapper.close();
        }
    }

    public void setLocalConfigPath(String configFile) {
        this.configFile = configFile;
    }

    public Map<String, RealDataSourceWrapper> getRealDSMap() {
        return realDSMap;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public RouterManager getRouterManager() {
        return routerManager;
    }
}
