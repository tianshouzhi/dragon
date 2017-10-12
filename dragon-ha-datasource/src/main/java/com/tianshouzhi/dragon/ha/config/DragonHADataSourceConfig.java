package com.tianshouzhi.dragon.ha.config;

import javax.sql.DataSource;
import java.util.Set;

/**
 * Created by tianshouzhi on 2017/10/9.
 */
public class DragonHADataSourceConfig {
    private Set<DragonRealDataSourceConfig> realDataSourceConfigMap;
    private Class<? extends DataSource> realDataSourceClass;

    public Class<? extends DataSource> getRealDataSourceClass() {
        return realDataSourceClass;
    }

    public void setRealDataSourceClass(Class<? extends DataSource> realDataSourceClass) {
        this.realDataSourceClass = realDataSourceClass;
    }

    public Set<DragonRealDataSourceConfig> getRealDataSourceConfigMap() {
        return realDataSourceConfigMap;
    }

    public void setRealDataSourceConfigMap(Set<DragonRealDataSourceConfig> realDataSourceConfigMap) {
        this.realDataSourceConfigMap = realDataSourceConfigMap;
    }
}
