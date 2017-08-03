package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.ha.config.DragonHAConfigParser;
import com.tianshouzhi.dragon.ha.config.DragonHAConfiguration;
import com.tianshouzhi.dragon.ha.jdbc.datasource.DragonHADatasource;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Created by tianshouzhi on 2017/6/7.
 */
public class DragonHADatasourceFactory {
	public DragonHADatasource build(String configFile) throws Exception {
		return build(ClassLoader.getSystemResourceAsStream(configFile));
	}

	public DragonHADatasource build(InputStream in) throws Exception {
		return build(DragonHAConfigParser.parse(in));
	}

	public DragonHADatasource build(DragonHAConfiguration configuration) throws Exception {
		return new DragonHADatasource(configuration);
	}
}
