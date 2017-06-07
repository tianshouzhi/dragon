package com.tianshouzhi.dragon.ha.config.spring;

import com.tianshouzhi.dragon.ha.config.DragonHADatasourceBuilder;
import com.tianshouzhi.dragon.ha.jdbc.datasource.DragonHADatasource;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by tianshouzhi on 2017/6/7.
 */
public class DragonHADatasourceFactoryBean implements FactoryBean<DragonHADatasource> {
    protected String configFile;
    @Override
    public DragonHADatasource getObject() throws Exception {
        return new DragonHADatasourceBuilder().build(configFile);
    }

    @Override
    public Class<?> getObjectType() {
        return DragonHADatasource.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }
}
