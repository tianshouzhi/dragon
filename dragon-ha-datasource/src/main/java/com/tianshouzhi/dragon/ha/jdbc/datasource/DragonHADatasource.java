package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.initailzer.DataSourceUtil;
import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSource;
import com.tianshouzhi.dragon.ha.config.RealDatasourceConfig;
import com.tianshouzhi.dragon.ha.config.DragonHAConfiguration;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import com.tianshouzhi.dragon.ha.jdbc.datasource.dbselector.RealDatasourceWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class DragonHADatasource extends DragonDataSource {
	private static final Logger LOGGER = LoggerFactory.getLogger(DragonHADatasource.class);

	private HADataSourceManager haDataSourceManager;

	private DragonHAConfiguration configuration;

	// must construct by DragonHADatasourceFactory
	public DragonHADatasource(DragonHAConfiguration configuration) throws Exception {
		init(configuration);
		this.configuration = configuration;
	}

	private void init(DragonHAConfiguration configuration) throws SQLException {
		HashMap<String, RealDatasourceWrapper> indexDSMap = new HashMap<String, RealDatasourceWrapper>();
		for (RealDatasourceConfig realDatasourceConfig : configuration.getRealDataSourceConfigList()) {
			String index = realDatasourceConfig.getIndex();
			RealDatasourceWrapper wrapper = new RealDatasourceWrapper(realDatasourceConfig);
			indexDSMap.put(index, wrapper);
		}

		if (!configuration.isLazyInit()) {
			for (RealDatasourceWrapper wrapper : indexDSMap.values()) {
				wrapper.init();
			}
		}

		this.haDataSourceManager = new HADataSourceManager();
		this.haDataSourceManager.refresh(indexDSMap);
	}

	@Override
	public DragonHAConnection getConnection(String username, String password) throws SQLException {
		return new DragonHAConnection(username, password, haDataSourceManager);
	}

	private synchronized void refreshConfig(DragonHAConfiguration newConfiguration) throws SQLException {
		// init(newConfiguration);
	}

	@Override
	public void close() throws SQLException {

		for (RealDatasourceWrapper realDatasourceWrapper : haDataSourceManager.getIndexDSMap().values()) {
			try {
				DataSourceUtil.close(realDatasourceWrapper.getRealDataSource());
			} catch (Exception e) {
				LOGGER.error("close datasource '" + realDatasourceWrapper + "' error", e);
			}
		}
	}
}
