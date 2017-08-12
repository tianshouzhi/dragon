package com.tianshouzhi.dragon.ha.config.manager;

import com.tianshouzhi.dragon.ha.config.DragonHADataSourceConfig;
import com.tianshouzhi.dragon.ha.exception.DragonHAException;
import com.tianshouzhi.dragon.ha.jdbc.datasource.DragonHADatasource;

/**
 * Created by tianshouzhi on 2017/8/11.
 */
public abstract class DragonHARemoteConfigurationManager implements DragonHAConfigurationManager {

    private DragonHADatasource dragonHADatasource;

    public DragonHARemoteConfigurationManager(DragonHADatasource dragonHADatasource) {
        this.dragonHADatasource = dragonHADatasource;
    }
    public void refresh(DragonHADataSourceConfig newConfiguration){
        try {
            dragonHADatasource.refreshConfig(newConfiguration);
        } catch (DragonHAException e) {
            e.printStackTrace();
        }
    }
}
