package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.log.Log;
import com.tianshouzhi.dragon.common.log.LoggerFactory;
import com.tianshouzhi.dragon.ha.config.RealDatasourceConfig;
import com.tianshouzhi.dragon.ha.exception.DragonHARuntimeException;
import com.tianshouzhi.dragon.ha.router.RouterManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by TIANSHOUZHI336 on 2016/12/3.
 */
public class RealDataSourceWrapperManager {
	private static final Log LOGGER = LoggerFactory.getLogger(RealDataSourceWrapperManager.class);

	private Map<String, RealDatasourceWrapper> validDSMap = new ConcurrentHashMap<String, RealDatasourceWrapper>();

	private Map<String, RealDatasourceWrapper> invalidDsMap = new ConcurrentHashMap<String, RealDatasourceWrapper>();

	private volatile boolean isRebuiding = false;

	private Lock rebuildLock = new ReentrantLock();

	private volatile RouterManager routerManager;

	public RealDataSourceWrapperManager(HashMap<String, RealDatasourceWrapper> datasourceWrapperMap) {
		refresh(datasourceWrapperMap, null, null);
		runInvalidRecoveryThread();
	}

	public void refresh(Map<String, RealDatasourceWrapper> needToAddMap,
	      Map<String, RealDatasourceWrapper> needToReplaceMap, Set<String> needToRemoveSet) {
		try {
			this.rebuildLock.lockInterruptibly();
			this.isRebuiding = true;
			add(needToAddMap);
			replace(needToReplaceMap);
			remove(needToRemoveSet);
			rebuildRouter();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			this.isRebuiding = false;
			rebuildLock.unlock();
		}
	}

	private synchronized void rebuildRouter() {
		Map<String, RealDatasourceConfig> validConfigMap = new HashMap<String, RealDatasourceConfig>(4);
		for (Map.Entry<String, RealDatasourceWrapper> entry : validDSMap.entrySet()) {
			String datasourceIndex = entry.getKey();
			RealDatasourceConfig config = entry.getValue().getConfig();
			validConfigMap.put(datasourceIndex, config);
		}
		this.routerManager = new RouterManager(validConfigMap);
	}

	private void add(Map<String, RealDatasourceWrapper> indexDSMap) {
		if (MapUtils.isNotEmpty(indexDSMap)) {
			LOGGER.info("add real datasources " + indexDSMap.keySet());
			this.validDSMap.putAll(indexDSMap);
		}
	}

	private void remove(Set<String> datasourceIndexes) {
		if (CollectionUtils.isNotEmpty(datasourceIndexes)) {
			LOGGER.info("clearThreadLocalHint real datasource" + datasourceIndexes);
			for (String datasourceIndex : datasourceIndexes) {
				Map<String, RealDatasourceWrapper> all = getIndexDSMap();
				RealDatasourceWrapper realDatasourceWrapper = all.get(datasourceIndex);
				this.validDSMap.remove(datasourceIndex);
				this.invalidDsMap.remove(datasourceIndex);
				if (realDatasourceWrapper != null) {
					realDatasourceWrapper.close();
				}
			}
		}
	}

	private void replace(Map<String, RealDatasourceWrapper> indexDSMap) {
		if (MapUtils.isNotEmpty(indexDSMap)) {
			LOGGER.info("repalce real datasource" + indexDSMap.keySet());
			remove(indexDSMap.keySet());
			add(indexDSMap);
		}
	}

	public String selectReadIndex(String... excludes) {
		checkRebuilding();
		return routerManager.routeRead(excludes);
	}

	private void checkRebuilding() {
		if (!isRebuiding) {
			return;
		} else {
			while (isRebuiding) {
				try {//wait 5 millis,or the cpu usage will be very high
					TimeUnit.MILLISECONDS.sleep(5);
				} catch (InterruptedException e) {
					throw new DragonHARuntimeException(e);
				}
			}
		}
	}

	public String selectWriteIndex(String... excludes) {
		checkRebuilding();
		return routerManager.routeWrite(excludes);
	}

	// specify dataSourceIndex ,no retry
	public Connection getConnectionByDbIndex(String dataSourceIndex, String username, String password)
	      throws SQLException {
		RealDatasourceWrapper realDatasourceWrapper = this.validDSMap.get(dataSourceIndex);
		if (realDatasourceWrapper == null) {
			throw new DragonHARuntimeException("not valid datasource found with dataSourceIndex:" + dataSourceIndex);
		}

		Connection connection = null;
		try {
			connection = realDatasourceWrapper.getConnection(username, password);
		} catch (SQLException e) {
			if (realDatasourceWrapper.getExceptionSorter().isExceptionFatal(e)) {
				invalid(realDatasourceWrapper.getConfig().getIndex());
			}
			throw e;
		}

		if (!connection.isReadOnly() && realDatasourceWrapper.isReadOnly()
		      && this.validDSMap.get(dataSourceIndex).isReadOnly()) {
			connection.setReadOnly(true);
		}
		return connection;
	}

	private void runInvalidRecoveryThread() {
		Thread recoveryThread = new Thread("DRAGON-HA-RecoveryThread") {
			@Override
			public void run() {

				while (true) {// 存在问题... cpu使用率必然变高，改成阻塞队列
					if (!invalidDsMap.isEmpty()) {
						for (Map.Entry<String, RealDatasourceWrapper> entry : invalidDsMap.entrySet()) {
							String dsIndex = entry.getKey();
							RealDatasourceWrapper realDatasourceWrapper = entry.getValue();
							DataSource realDataSource = realDatasourceWrapper.getRealDataSource();
							try {
								Connection connection = realDataSource.getConnection();
								if (connection.isValid(3000)) {
									LOGGER.info("datasource 【" + dsIndex + "】 became valid");
									RealDataSourceWrapperManager.this.invalidDsMap.remove(dsIndex);
									RealDataSourceWrapperManager.this.validDSMap.put(dsIndex, realDatasourceWrapper);
									rebuildRouter();
								}
							} catch (SQLException e) {
								LOGGER.debug("datasource 【" + dsIndex + "】 still invalid");
							}
						}
					}
				}
			}
		};
		recoveryThread.setDaemon(true);
		recoveryThread.start();
	}

	public void invalid(String dataSourceIndex) {// fixme optimize need lock
		checkRebuilding();
		RealDatasourceWrapper realDatasourceWrapper = this.validDSMap.get(dataSourceIndex);
		if (realDatasourceWrapper != null) {
			LOGGER.warn("datasource 【" + dataSourceIndex + "】 became invalid!!");
			this.invalidDsMap.put(dataSourceIndex, realDatasourceWrapper);
			this.validDSMap.remove(dataSourceIndex);
			rebuildRouter();
		}
	}

	public Map<String, RealDatasourceWrapper> getIndexDSMap() {
		Map<String, RealDatasourceWrapper> map = new HashMap<String, RealDatasourceWrapper>(4);
		map.putAll(validDSMap);
		map.putAll(invalidDsMap);
		return Collections.unmodifiableMap(map);
	}

	public Map<String, RealDatasourceWrapper> getValidDSMap() {
		return Collections.unmodifiableMap(validDSMap);
	}

	public Map<String, RealDatasourceWrapper> getInvalidDsMap() {
		return Collections.unmodifiableMap(invalidDsMap);
	}
}
