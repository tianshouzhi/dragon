package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.initailzer.DataSourceInitailzerUtil;
import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSource;
import com.tianshouzhi.dragon.ha.config.RealDatasourceConfig;
import com.tianshouzhi.dragon.ha.config.DragonHAConfiguration;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import com.tianshouzhi.dragon.ha.jdbc.datasource.dbselector.DatasourceWrapper;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class DragonHADatasource extends DragonDataSource {
	private HADataSourceManager haDataSourceManager;

	public DragonHADatasource(DragonHAConfiguration configuration) throws Exception {
		haDataSourceManager = new HADataSourceManager();
		List<RealDatasourceConfig> dsConfigList = configuration.getDsConfigList();
		HashMap<String, DatasourceWrapper> indexDSMap = new HashMap<String, DatasourceWrapper>();
		for (RealDatasourceConfig realDatasourceConfig : dsConfigList) {
			String realClass = realDatasourceConfig.getRealClass();
			int readWeight = realDatasourceConfig.getReadWeight();
			Integer writeWeight = realDatasourceConfig.getWriteWeight();
			String index = realDatasourceConfig.getIndex();
			List<RealDatasourceConfig.Property> properties = realDatasourceConfig.getProperties();
			DataSource dataSource = DataSourceInitailzerUtil.init(realClass,
			      RealDatasourceConfig.propertiesToMap(properties));
			indexDSMap.put(index, new DatasourceWrapper(index, readWeight, writeWeight, dataSource));
		}
		haDataSourceManager.setIndexDSMap(indexDSMap);
		haDataSourceManager.rebuild();
	}

	@Override
	public DragonHAConnection getConnection(String username, String password) throws SQLException {
		return new DragonHAConnection(username, password, haDataSourceManager);
	}
}
