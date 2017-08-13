package com.tianshouzhi.dragon.ha.config.manager;

import com.tianshouzhi.dragon.ha.config.DragonHAConfiguration;
import com.tianshouzhi.dragon.ha.exception.DragonHAConfigException;
import com.tianshouzhi.dragon.ha.jdbc.datasource.DragonHADatasource;

/**
 * Created by tianshouzhi on 2017/8/11.
 */
public interface DragonHAConfigurationManager {
     DragonHAConfiguration getConfiguration() throws DragonHAConfigException;

     /**
      * if configuration change,you can invoke {@link com.tianshouzhi.dragon.ha.jdbc.datasource.DragonHADatasource#refreshConfig(com.tianshouzhi.dragon.ha.config.DragonHAConfiguration)}
      * @param dataSource
      */
     void setDragonHADataSource(DragonHADatasource dataSource);
}
