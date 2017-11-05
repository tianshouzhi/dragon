package com.tianshouzhi.dragon.ha.config;

import com.tianshouzhi.dragon.common.exception.DragonException;
import com.tianshouzhi.dragon.common.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by tianshouzhi on 2017/11/2.
 */
public class HALocalConfigManager implements HAConfigManager {

	private String configFile;

	public HALocalConfigManager(String configFile) {
		if (StringUtils.isBlank(configFile)) {
			throw new NullPointerException("configFile can't be blank!");
		}
		this.configFile = configFile;
	}

	@Override
	public HADataSourceConfig getHADataSourceConfig() {
		InputStream in = HALocalConfigManager.class.getClassLoader().getResourceAsStream(configFile);
		Properties properties = new Properties();
		try {
			properties.load(in);
		} catch (IOException e) {
			throw new DragonException("load config from classpath:" + configFile + " error!");
		}
		return new HADataSourceConfig(properties);
	}
}
