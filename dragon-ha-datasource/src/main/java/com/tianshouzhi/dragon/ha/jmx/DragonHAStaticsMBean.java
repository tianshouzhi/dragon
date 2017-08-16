package com.tianshouzhi.dragon.ha.jmx;

import com.tianshouzhi.dragon.ha.config.DragonHAConfiguration;

import java.util.Map;

/**
 * Created by tianshouzhi on 2017/8/12.
 */
public class DragonHAStaticsMBean {
	private String verison;

	private DragonHAConfiguration configuration;

	private Map<String, RealDataSourceMBean> staticsMap;

}
