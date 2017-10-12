package com.tianshouzhi.dragon.ha.config;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by tianshouzhi on 2017/10/9.
 */
public class DragonRealDataSourceConfig {
    private String index;
    private int readWeight;
    private int writeWeight;
    private Class<? extends DataSource> realDataSourceClass;
    private Properties properties;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public int getReadWeight() {
        return readWeight;
    }

    public void setReadWeight(int readWeight) {
        this.readWeight = readWeight;
    }

    public int getWriteWeight() {
        return writeWeight;
    }

    public void setWriteWeight(int writeWeight) {
        this.writeWeight = writeWeight;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Class<? extends DataSource> getRealDataSourceClass() {
        return realDataSourceClass;
    }

    public void setRealDataSourceClass(Class<? extends DataSource> realDataSourceClass) {
        this.realDataSourceClass = realDataSourceClass;
    }
}
