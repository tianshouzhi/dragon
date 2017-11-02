package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSourceAdapter;
import com.tianshouzhi.dragon.common.log.Log;
import com.tianshouzhi.dragon.common.log.LoggerFactory;
import com.tianshouzhi.dragon.ha.exception.DragonHAException;
import com.tianshouzhi.dragon.ha.exception.DragonHARuntimeException;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import com.tianshouzhi.dragon.ha.router.RouterManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class DragonHADatasource extends DragonDataSourceAdapter {
	private static final Log LOGGER = LoggerFactory.getLogger(DragonHADatasource.class);

	private Map<String, RealDataSourceWapper> dsWrappers = new ConcurrentHashMap<String, RealDataSourceWapper>(4);

	private boolean lazyInit = true;

	private volatile RouterManager routerManager;

	@Override
	protected void doInit() throws Exception {
		if (dsWrappers.isEmpty()) {
			throw new DragonHAException("dsWrappers can't be null!");
		}
		if (!lazyInit) {
			for (RealDataSourceWapper realDataSourceWapper : dsWrappers.values()) {
				realDataSourceWapper.init();
			}
		}
		this.routerManager = new RouterManager(this.dsWrappers);
	}

	@Override
	protected DragonHAConnection doGetConnection(String username, String password) throws SQLException {
		return new DragonHAConnection(username, password, this);
	}

	@Override
	public void close() throws Exception {
		LOGGER.info("close dragon ha datasource start ...");
		for (RealDataSourceWapper realDataSourceWrapper : this.dsWrappers.values()) {
			LOGGER.info("close real datasource[" + realDataSourceWrapper.getIndex() + "]...");
			realDataSourceWrapper.close();
			LOGGER.info("close real datasource[" + realDataSourceWrapper.getIndex() + "] success...");
		}
		LOGGER.info("close dragon ha datasource ...");
	}

	public void addRealDatasource(String index, int readWeight, int writeWeight, DataSource dataSource) {
		if(!init){
			dsWrappers.put(index, new RealDataSourceWapper(index, readWeight, writeWeight, dataSource));
		}else{

		}
	}

	public RouterManager getRouterManager() {
		return routerManager;
	}

	public Connection getConnectionByDbIndex(String index) throws SQLException {
		RealDataSourceWapper realDataSourceWrapper = this.dsWrappers.get(index);
		if (realDataSourceWrapper == null) {
			throw new DragonHARuntimeException("not valid datasource found with index:" + index);
		}
		return realDataSourceWrapper.getRealDataSource().getConnection();
	}
}
