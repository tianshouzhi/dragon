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

	private String localConfigPath;

	private HADataSourceConfig haConfig;

	public HALocalConfigManager(String localConfigPath) {
		if (StringUtils.isBlank(localConfigPath)) {
			throw new NullPointerException("localConfigPath can't be blank!");
		}
		this.localConfigPath = localConfigPath;
	}

	@Override
	public HADataSourceConfig getHADataSourceConfig() {
		if (haConfig != null) {
			return haConfig;
		}
		InputStream in = HALocalConfigManager.class.getClassLoader().getResourceAsStream(localConfigPath);
		Properties properties = new Properties();
		try {
			properties.load(in);
		} catch (IOException e) {
			throw new DragonException("load config from classpath:" + localConfigPath + " error!");
		}
		return new HADataSourceConfig(properties);
	}
}
