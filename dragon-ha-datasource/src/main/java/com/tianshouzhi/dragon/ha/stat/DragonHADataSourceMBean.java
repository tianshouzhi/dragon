package com.tianshouzhi.dragon.ha.stat;

import com.tianshouzhi.dragon.common.util.MavenVersionUtil;
import com.tianshouzhi.dragon.ha.config.DragonHAConfiguration;

import java.util.Map;

/**
 * Created by tianshouzhi on 2017/8/12.
 */
public class DragonHADataSourceMBean {
	private String verison = MavenVersionUtil.getVersion(this.getClass());

	private String maxActive;

	private DragonHAConfiguration configuration;

	private Map<String, RealDataSourceMBean> staticsMap;

}
