package com.tianshouzhi.dragon.ha.config;

import com.tianshouzhi.dragon.common.exception.DragonException;
import com.tianshouzhi.dragon.common.util.StringUtils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by tianshouzhi on 2017/11/2.
 */
public class HADataSourceConfig {
	private Properties properties;

	private Map<String, RealDsWrapperConfig> realDataSourceConfigMap = new HashMap<String, RealDsWrapperConfig>(4);

	public HADataSourceConfig(Properties properties) {
		this.properties = properties;
		String dsNames = properties.getProperty("dragon.ha.datasources");
		if (StringUtils.isBlank(dsNames)) {
			throw new DragonException("dragon.ha.datasources can't be null");
		}
		for (String dsName : dsNames.split(",")) {
			String _dsName = dsName.trim();
			String dsClass = properties.getProperty("dragon.ha." + _dsName + ".class");
			String readWeight = properties.getProperty("dragon.ha." + _dsName + ".readWeight", "0");
			String writeWeight = properties.getProperty("dragon.ha." + _dsName + ".writeWeight", "0");
			Properties dsProperties = new Properties();
			Enumeration<?> enumeration = properties.propertyNames();
			while (enumeration.hasMoreElements()) {
				String propertyName = (String) enumeration.nextElement();
				String propertyPrefix = "dragon.ha." + _dsName + ".property.";
				if (propertyName.startsWith(propertyPrefix)) {
					String dsPropertyName = propertyName.substring(propertyPrefix.length() );
					String dsPropertyValue = (String) properties.get(propertyName);
					dsProperties.put(dsPropertyName, dsPropertyValue);
				}
			}
			RealDsWrapperConfig realDataSourceConfig = new RealDsWrapperConfig();
			realDataSourceConfig.setRealDsName(_dsName);
			realDataSourceConfig.setRealDsClass(dsClass);
			realDataSourceConfig.setReadWeight(Integer.parseInt(readWeight));
			realDataSourceConfig.setWriteWeight(Integer.parseInt(writeWeight));
			realDataSourceConfig.setRealDsProperties(dsProperties);
			realDataSourceConfigMap.put(dsName, realDataSourceConfig);
		}
	}

	public Properties getProperties() {
		return properties;
	}

	public Map<String, RealDsWrapperConfig> getRealDataSourceConfigMap() {
		return realDataSourceConfigMap;
	}

}
