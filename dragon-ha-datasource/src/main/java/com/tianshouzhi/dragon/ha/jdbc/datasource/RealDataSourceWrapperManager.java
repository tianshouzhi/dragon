package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.log.Log;
import com.tianshouzhi.dragon.common.log.LoggerFactory;
import com.tianshouzhi.dragon.ha.exception.DragonHARuntimeException;
import com.tianshouzhi.dragon.ha.router.weight.DBSelector;
import com.tianshouzhi.dragon.ha.router.weight.ReadDBSelector;
import com.tianshouzhi.dragon.ha.router.weight.WriteDBSelector;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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

	private DBSelector readDBSelector;

	private DBSelector writeDBSelector;

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
		ReadDBSelector readDBSelector = new ReadDBSelector(this.validDSMap);
		WriteDBSelector writeDBSelector = new WriteDBSelector(this.validDSMap);
		this.readDBSelector = readDBSelector;
		this.writeDBSelector = writeDBSelector;
	}

	private void add(Map<String, RealDatasourceWrapper> indexDSMap) {
		if (MapUtils.isNotEmpty(indexDSMap)) {
			LOGGER.info("add real datasources " + indexDSMap.keySet());
			this.validDSMap.putAll(indexDSMap);
		}
	}

	private void remove(Set<String> datasourceIndexes) {
		if (CollectionUtils.isNotEmpty(datasourceIndexes)) {
			LOGGER.info("remove real datasource" + datasourceIndexes);
			for (String datasourceIndex : datasourceIndexes) {
				RealDatasourceWrapper realDatasourceWrapper = getIndexDSMap().get(datasourceIndex);
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

	public String selectWriteDBIndex() {
		while (!isRebuiding)
			break;
		return writeDBSelector.select();
	}

	public String selectReadDBIndex() {
		while (!isRebuiding)
			break;
		return readDBSelector.select();
	}

	public String selectReadDBIndexExclude(Set<String> excludes) {
		Set<String> managedDataSourceIndices = readDBSelector.getManagedDBIndexes();
		managedDataSourceIndices.removeAll(excludes);
		if (managedDataSourceIndices.isEmpty()) {
			return null;
		}
		return managedDataSourceIndices.iterator().next();
	}

	public RealDatasourceWrapper getDatasourceWrapperByDbIndex(String dataSourceIndex) {
		while (!isRebuiding)
			break;
		return validDSMap.get(dataSourceIndex);
	}

	public Connection getConnectionByDbIndex(String dataSourceIndex, String username, String password)
	      throws SQLException {
		while (!isRebuiding)
			break;
		RealDatasourceWrapper realDatasourceWrapper = validDSMap.get(dataSourceIndex);
		if (realDatasourceWrapper == null) {
			throw new DragonHARuntimeException("not found datasource with dataSourceIndex:" + dataSourceIndex);
		}
		DataSource realDataSource = realDatasourceWrapper.getRealDataSource();
		Connection connection = null;

		if (StringUtils.isAnyBlank(username, password))
			connection = realDataSource.getConnection();
		else
			connection = realDataSource.getConnection(username, password);// druid不支持这个方法
		if (!connection.isReadOnly() && realDatasourceWrapper.isReadOnly()
		      && this.validDSMap.get(dataSourceIndex).isReadOnly()) {
			connection.setReadOnly(true);
		}
		return connection;
	}

	public Connection getConnectionByDbIndex(List<String> hintDataSourceIndices, String username, String password)
	      throws SQLException {
		while (!isRebuiding)
			break;
		if (CollectionUtils.isEmpty(hintDataSourceIndices)) {
			throw new SQLException("hintDataSourceIndices can't be bull or empty");
		}
		if (hintDataSourceIndices.size() == 1) {
			return getConnectionByDbIndex(hintDataSourceIndices.get(0), username, password);
		}
		int randomIndex = new Random().nextInt(hintDataSourceIndices.size());
		return getConnectionByDbIndex(hintDataSourceIndices.get(randomIndex), username, password);
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

	public void invalid(String dataSourceIndex) {
		while (!this.isRebuiding)
			break;
		if (this.validDSMap.get(dataSourceIndex) != null) {
			RealDatasourceWrapper realDatasourceWrapper = this.validDSMap.get(dataSourceIndex);
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
