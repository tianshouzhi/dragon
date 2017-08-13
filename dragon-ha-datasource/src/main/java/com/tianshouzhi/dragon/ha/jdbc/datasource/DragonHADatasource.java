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
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                try {
                    close();
                } catch (SQLException ignore) {
                }
            }
        });
    }

    public synchronized void refreshConfig(DragonHAConfiguration newConfiguration) throws DragonHAException {
        if (newConfiguration == null || configuration.equals(newConfiguration)) {
            return;
        }
        StringBuilder refreshMsg = new StringBuilder(200);
        refreshMsg.append("refresh ha config .\n");
        refreshMsg.append("======================origin ha config==================.\n");
        refreshMsg.append(DragonHAXmlConfigParser.toXml(configuration));
        refreshMsg.append("======================new ha config==================.\n");
        refreshMsg.append(DragonHAXmlConfigParser.toXml(newConfiguration));

        try {
            checkDragonHAConfiguration(newConfiguration);
            HashMap<String, RealDatasourceWrapper> newDataSourceWrapperMap = getDatasourceWrapperMap(newConfiguration);
            realDataSourceManager.refresh(newDataSourceWrapperMap);
            this.configuration = newConfiguration;
        } catch (Throwable e) {
            throw new DragonHAConfigException("refresh ha config error,will keep the origin config", e);
        }
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
                    "'" + index + "' config error, both 'readWeight' and 'writeWeight' can't less than zero," +
                            "current readWeight:" + readWeight + ",current writeWeight:" + writeWeight);
        }

        try {
            DataSourceUtil.checkConnection(config.getRealClass(), config.getPropertiesMap());
        } catch (SQLException e) {
            throw new DragonHAConfigException("'" + index + "'check connection error ,please check config【" + config + "】", e);
        }
    }

    private HashMap<String, RealDatasourceWrapper> getDatasourceWrapperMap(DragonHAConfiguration configuration) throws DragonHAException {
        HashMap<String, RealDatasourceWrapper> datasourceWrapperMap = new HashMap<String, RealDatasourceWrapper>();
        for (RealDatasourceConfig realDatasourceConfig : configuration.getRealDataSourceConfigList()) {
            String index = realDatasourceConfig.getIndex();
            RealDatasourceWrapper wrapper = new RealDatasourceWrapper(realDatasourceConfig);
            datasourceWrapperMap.put(index, wrapper);
        }

        return datasourceWrapperMap;
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
}
