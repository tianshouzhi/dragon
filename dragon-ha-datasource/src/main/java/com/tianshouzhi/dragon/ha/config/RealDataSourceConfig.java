package com.tianshouzhi.dragon.ha.config;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by tianshouzhi on 2017/10/13.
 */
public class RealDataSourceConfig {
    private String index;
    private int readWeight;
    private int writeWeight;
    private Class<? extends DataSource> realDsClass;
    private Properties realDsProperties;
}
