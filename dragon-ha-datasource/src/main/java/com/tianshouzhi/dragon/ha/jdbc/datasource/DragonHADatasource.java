package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.initailzer.DataSourceUtil;
import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSource;
import com.tianshouzhi.dragon.common.log.Log;
import com.tianshouzhi.dragon.common.log.LoggerFactory;
import com.tianshouzhi.dragon.ha.config.DragonHADataSourceConfig;
import com.tianshouzhi.dragon.ha.config.RealDatasourceConfig;
import com.tianshouzhi.dragon.ha.config.manager.DragonHAConfigurationManager;
import com.tianshouzhi.dragon.ha.config.manager.DragonHALocalConfigurationManager;
import com.tianshouzhi.dragon.ha.exception.DragonHAConfigException;
import com.tianshouzhi.dragon.ha.exception.DragonHAException;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class DragonHADatasource extends DragonDataSource {
    private static final Log LOGGER = LoggerFactory.getLogger(DragonHADatasource.class);

    private RealDataSourceWrapperManager realDataSourceManager;
    private DragonHAConfigurationManager configurationManager;
    private DragonHADataSourceConfig configuration;

    public DragonHADatasource(String configFile) throws DragonHAException {
        this(new DragonHALocalConfigurationManager(configFile));
    }

    public DragonHADatasource(DragonHAConfigurationManager configurationManager) throws DragonHAException {
        if (configurationManager == null) {
            throw new NullPointerException("parameter 'configurationManager' can't be null");
        }
        DragonHADataSourceConfig configuration = configurationManager.getConfiguration();
        checkDragonHAConfiguration(configuration);

        HashMap<String, RealDatasourceWrapper> datasourceWrapperMap = getDatasourceWrapperMap(configuration);
        if (!configuration.isLazyInit()) {
            for (RealDatasourceWrapper wrapper : datasourceWrapperMap.values()) {
                wrapper.init();
            }
        }
        this.realDataSourceManager = new RealDataSourceWrapperManager(datasourceWrapperMap);
        this.configurationManager = configurationManager;
        this.configuration = configuration;
    }

    private void checkDragonHAConfiguration(DragonHADataSourceConfig configuration) throws DragonHAConfigException {
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
                    "'" + index + "' config error, both 'readWeight' and 'writeWeight' can't less than zero," +
                            "current readWeight:" + readWeight + ",current writeWeight:" + writeWeight);
        }

        try {
            DataSourceUtil.checkConnection(config.getRealClass(), config.getPropertiesMap());
        } catch (SQLException e) {
            throw new DragonHAConfigException("'" + index + "'check connection error ,please check config【" + config + "】", e);
        }
    }

    public synchronized void refreshConfig(DragonHADataSourceConfig newConfiguration) throws DragonHAException {
        if (newConfiguration == null || configuration.equals(newConfiguration)) {
            return;
        }
        checkDragonHAConfiguration(newConfiguration);
        HashMap<String, RealDatasourceWrapper> newDataSourceWrapperMap = getDatasourceWrapperMap(newConfiguration);
        realDataSourceManager.refresh(newDataSourceWrapperMap);
        this.configuration = newConfiguration;
    }

    private HashMap<String, RealDatasourceWrapper> getDatasourceWrapperMap(DragonHADataSourceConfig configuration) throws DragonHAException {
        HashMap<String, RealDatasourceWrapper> indexDSMap = new HashMap<String, RealDatasourceWrapper>();
        for (RealDatasourceConfig realDatasourceConfig : configuration.getRealDataSourceConfigList()) {
            String index = realDatasourceConfig.getIndex();
            RealDatasourceWrapper wrapper = new RealDatasourceWrapper(realDatasourceConfig);
            indexDSMap.put(index, wrapper);
        }
        return indexDSMap;
    }

    @Override
    public DragonHAConnection getConnection(String username, String password) throws SQLException {
        return new DragonHAConnection(username, password, realDataSourceManager);
    }

    @Override
    public void close() throws SQLException {
        for (RealDatasourceWrapper realDatasourceWrapper : realDataSourceManager.getIndexDSMap().values()) {
            try {
                DataSourceUtil.close(realDatasourceWrapper.getRealDataSource());
            } catch (Exception e) {
                LOGGER.error("close datasource '" + realDatasourceWrapper + "' error", e);
            }
        }
    }
}
