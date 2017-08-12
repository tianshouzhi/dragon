package com.tianshouzhi.dragon.ha.jmx;

import com.tianshouzhi.dragon.ha.config.DragonHADataSourceConfig;

import java.util.Map;

/**
 * Created by tianshouzhi on 2017/8/12.
 */
public class DragonHAStaticsMBean {
    private String verison;
    private DragonHADataSourceConfig configuration;
    private Map<String,RealDataSourceMBean> staticsMap;

}
