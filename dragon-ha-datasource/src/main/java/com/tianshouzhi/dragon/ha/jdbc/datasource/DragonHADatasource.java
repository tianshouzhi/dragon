package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSourceAdapter;
import com.tianshouzhi.dragon.common.log.Log;
import com.tianshouzhi.dragon.common.log.LoggerFactory;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import com.tianshouzhi.dragon.real.jdbc.RealDataSource;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class DragonHADatasource extends DragonDataSourceAdapter {
	private static final Log LOGGER = LoggerFactory.getLogger(DragonHADatasource.class);

	private RealDataSourceManager realDataSourceManager;

	private Map<String, RealDataSource> realDataSourceMap = new ConcurrentHashMap<String, RealDataSource>(4);

	private volatile boolean inited = false;

	@Override
	public synchronized void init() throws SQLException {
		if (!inited) {
			if (realDataSourceMap.isEmpty()) {

			}
			realDataSourceManager = new RealDataSourceManager(realDataSourceMap);
			inited = true;
		}
	}

	@Override
	public DragonHAConnection getConnection(String username, String password) throws SQLException {
		init();
		return new DragonHAConnection(username, password, realDataSourceManager);
	}

	@Override
	public void close() throws SQLException {
		LOGGER.info("close dragon ha datasource start ...");
		for (RealDataSource realDataSource : this.realDataSourceMap.values()) {
			LOGGER.info("close real datasource[" + realDataSource.getIndex() + "]...");
			realDataSource.close();
			LOGGER.info("close real datasource[" + realDataSource.getIndex() + "] end...");
		}
		LOGGER.info("close dragon ha datasource ...");
	}

	public void setRealDataSources(Set<RealDataSource> realDataSourceSet) {
		for (RealDataSource realDataSource : realDataSourceSet) {
			this.realDataSourceMap.put(realDataSource.getIndex(),realDataSource);
		}
	}
}
