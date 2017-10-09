package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.log.Log;
import com.tianshouzhi.dragon.common.log.LoggerFactory;
import com.tianshouzhi.dragon.common.util.MapUtils;
import com.tianshouzhi.dragon.ha.exception.DragonHARuntimeException;
import com.tianshouzhi.dragon.ha.router.RouterManager;
import com.tianshouzhi.dragon.real.jdbc.RealDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by TIANSHOUZHI336 on 2016/12/3.
 */
public class RealDataSourceManager {
	private static final Log LOGGER = LoggerFactory.getLogger(RealDataSourceManager.class);

	private Map<String, RealDataSource> realDataSourceMap;

	private volatile boolean isRebuiding = false;

	private Lock rebuildLock = new ReentrantLock();

	private volatile RouterManager routerManager;

	public RealDataSourceManager(Map<String, RealDataSource> realDataSourceMap) {
		if (MapUtils.isEmpty(realDataSourceMap)) {
			throw new DragonHARuntimeException("realDataSourceMap can't be empty!");
		}
		this.realDataSourceMap = realDataSourceMap;
		refresh(realDataSourceMap, null, null);
	}

	public void refresh(Map<String, RealDataSource> needToAddMap, Map<String, RealDataSource> needToReplaceMap,
	      Set<String> needToRemoveSet) {
		try {
			this.rebuildLock.lockInterruptibly();
			this.isRebuiding = true;
			rebuildRouter();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			this.isRebuiding = false;
			rebuildLock.unlock();
		}
	}

	private synchronized void rebuildRouter() {
		this.routerManager = new RouterManager(this.realDataSourceMap);
		LOGGER.info("build routerManager success!!!");
	}

	public String selectReadIndex(Set<String> excludes) {
		checkRebuilding();
		return routerManager.routeRead(excludes);
	}

	private void checkRebuilding() {
		if (!isRebuiding) {
			return;
		} else {
			while (isRebuiding) {
				try {// wait 5 millis
					TimeUnit.MILLISECONDS.sleep(5);
				} catch (InterruptedException e) {
					throw new DragonHARuntimeException(e);
				}
			}
		}
	}

	public String selectWriteIndex(Set<String> excludes) {
		checkRebuilding();
		return routerManager.routeWrite(excludes);
	}

	public Connection getConnectionByDbIndex(String index) throws SQLException {
		RealDataSource realDataSource = this.realDataSourceMap.get(index);
		if (realDataSource == null) {
			throw new DragonHARuntimeException("not valid datasource found with index:" + index);
		}

		return realDataSource.getConnection();
	}
}
