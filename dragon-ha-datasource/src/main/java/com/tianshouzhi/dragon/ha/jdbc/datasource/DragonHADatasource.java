package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.exception.DragonException;
import com.tianshouzhi.dragon.common.initailzer.DataSourceUtil;
import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSource;
import com.tianshouzhi.dragon.ha.config.RealDatasourceConfig;
import com.tianshouzhi.dragon.ha.config.DragonHAConfiguration;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import com.tianshouzhi.dragon.ha.jdbc.datasource.dbselector.RealDatasourceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class DragonHADatasource extends DragonDataSource {
	private static final Logger LOGGER = LoggerFactory.getLogger(DragonHADatasource.class);

	private HADataSourceManager haDataSourceManager;

	private DragonHAConfiguration configuration;

	private boolean lazyInit=true;

	public DragonHADatasource(DragonHAConfiguration configuration) throws Exception {
		init(configuration);
		this.configuration = configuration;
	}

	private void init(DragonHAConfiguration configuration) throws SQLException {
		haDataSourceManager = new HADataSourceManager();
		List<RealDatasourceConfig> dsConfigList = configuration.getRealDataSourceConfigList();
		HashMap<String, RealDatasourceWrapper> indexDSMap = new HashMap<String, RealDatasourceWrapper>();
		for (RealDatasourceConfig realDatasourceConfig : dsConfigList) {
			String realClass = realDatasourceConfig.getRealClass();
			String index = realDatasourceConfig.getIndex();

			DataSource dataSource = null;
			RealDatasourceWrapper wrapper = new RealDatasourceWrapper(realDatasourceConfig);
			if(!lazyInit){
				wrapper.init();
			}
			indexDSMap.put(index, wrapper);
		}
		haDataSourceManager.setIndexDSMap(indexDSMap);
		haDataSourceManager.rebuild();

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
