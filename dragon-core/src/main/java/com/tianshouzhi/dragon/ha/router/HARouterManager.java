package com.tianshouzhi.dragon.ha.router;

import com.tianshouzhi.dragon.common.util.MapUtils;
import com.tianshouzhi.dragon.ha.exception.HASQLException;
import com.tianshouzhi.dragon.ha.jdbc.HADatasource;
import com.tianshouzhi.dragon.ha.jdbc.RealDsWrapper;
import com.tianshouzhi.dragon.ha.router.impl.SingleRouter;
import com.tianshouzhi.dragon.ha.router.impl.WeightRouter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tianshouzhi on 2017/8/16.
 */
public class HARouterManager {

	private final Router readRouter;

	private final Router writeRouter;

	private String haDSName;

	public HARouterManager(HADatasource haDatasource) {

		this.haDSName = haDatasource.getHaDSName();
		Map<String, RealDsWrapper> dataSourceWrappers = haDatasource.getRealDSWrapperMap();
		this.readRouter = buildRouter(dataSourceWrappers, true);
		this.writeRouter = buildRouter(dataSourceWrappers, false);
	}

	private Router buildRouter(Map<String, RealDsWrapper> realDataSourceWrapperMap, boolean isRead) {
		Map<String, RealDsWrapper> wrapperMap = filterDatasourceConfig(realDataSourceWrapperMap, isRead);
		if (MapUtils.isEmpty(wrapperMap)) {
			return null;
		}

		if (wrapperMap.size() == 1) {
			return buildSingleRouter(wrapperMap);
		}
		return buildWeightRouter(wrapperMap, isRead);
	}

	private Router buildSingleRouter(Map<String, RealDsWrapper> configMap) {
		return new SingleRouter(this.haDSName, configMap.keySet().iterator().next());
	}

	private Router buildWeightRouter(Map<String, RealDsWrapper> dataSourceWrappers, boolean isRead) {
		HashMap<String, Integer> dsWeightMap = new HashMap<String, Integer>(4);
		for (Map.Entry<String, RealDsWrapper> entry : dataSourceWrappers.entrySet()) {
			String realDSName = entry.getKey();
			RealDsWrapper realDataSourceWrapper = entry.getValue();
			if (isRead && realDataSourceWrapper.getReadWeight() > 0) {
				dsWeightMap.put(realDSName, realDataSourceWrapper.getReadWeight());
			}
			if (!isRead && realDataSourceWrapper.getWriteWeight() > 0) {
				dsWeightMap.put(realDSName, realDataSourceWrapper.getWriteWeight());
			}
		}
		if (dsWeightMap.size() == 0) {
			return null;
		}
		return new WeightRouter(this.haDSName, dsWeightMap);
	}

	private Map<String, RealDsWrapper> filterDatasourceConfig(Map<String, RealDsWrapper> configMap, boolean isread) {
		Map<String, RealDsWrapper> filterResult = new HashMap<String, RealDsWrapper>(4);
		for (Map.Entry<String, RealDsWrapper> configEntry : configMap.entrySet()) {
			String datasourceIndex = configEntry.getKey();
			RealDsWrapper config = configEntry.getValue();
			if (isread) {
				if (config.getReadWeight() > 0) {
					filterResult.put(datasourceIndex, config);
				}
			} else {
				if (config.getWriteWeight() > 0) {
					filterResult.put(datasourceIndex, config);
				}
			}
		}
		return filterResult;
	}

	public String routeWrite() {
		if (writeRouter == null) {
			throw new HASQLException("writeRouter is null");
		}
		return writeRouter.route();
	}

	public String routeRead() {
		if (readRouter == null) {
			throw new HASQLException("readRouter is null");
		}
		return readRouter.route();
	}
}
