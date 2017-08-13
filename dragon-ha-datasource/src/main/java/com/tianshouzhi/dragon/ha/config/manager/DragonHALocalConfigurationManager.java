package com.tianshouzhi.dragon.ha.config.manager;

import com.tianshouzhi.dragon.common.log.Log;
import com.tianshouzhi.dragon.common.log.LoggerFactory;
import com.tianshouzhi.dragon.common.thread.DragonThreadFactory;
import com.tianshouzhi.dragon.ha.config.DragonHAConfiguration;
import com.tianshouzhi.dragon.ha.config.parser.DragonHAXmlConfigParser;
import com.tianshouzhi.dragon.ha.exception.DragonHAConfigException;
import com.tianshouzhi.dragon.ha.jdbc.datasource.DragonHADatasource;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by tianshouzhi on 2017/8/11.
 */
public class DragonHALocalConfigurationManager implements DragonHAConfigurationManager {
    private static final Log LOGGER=LoggerFactory.getLogger(DragonHALocalConfigurationManager.class);
    private ScheduledExecutorService executorService;
    private String configFile;
    private boolean checkLocalChange;
    private DragonHAConfiguration snapshot;
    private DragonHADatasource dataSource;

    public DragonHALocalConfigurationManager(String configFile) throws DragonHAConfigException {
        this(configFile,false);
    }
    public DragonHALocalConfigurationManager(String configFile,boolean checkLocalChange) throws DragonHAConfigException {
        this.configFile = configFile;
        this.snapshot=loadConfigFromLocal();
        this.checkLocalChange=checkLocalChange;
        if(this.checkLocalChange){
            executorService = Executors.newSingleThreadScheduledExecutor(new DragonThreadFactory("DRAGON-HA-LOCAL-CONFIG-CHECK-THREAD"));
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        if(dataSource!=null){
                            DragonHAConfiguration configuration = loadConfigFromLocal();
                            dataSource.refreshConfig(configuration);
                        }
                    } catch (Throwable e) {
                        LOGGER.error("refresh dragon ha config error",e);
                    }
                }
            };
            executorService.scheduleWithFixedDelay(runnable,10L,10L, TimeUnit.SECONDS);
        }
    }

    @Override
    public DragonHAConfiguration getConfiguration() throws DragonHAConfigException {
        return snapshot;
    }

    private DragonHAConfiguration loadConfigFromLocal() throws DragonHAConfigException {
        InputStream inputStream = null;
        try {
            inputStream = DragonHALocalConfigurationManager.class.getClassLoader().getResourceAsStream(this.configFile);
            return DragonHAXmlConfigParser.parse(inputStream);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    @Override
    public void setDragonHADataSource(DragonHADatasource dataSource) {
        this.dataSource = dataSource;
    }
}
