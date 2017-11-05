package com.tianshouzhi.dragon.ha.router;

import com.tianshouzhi.dragon.common.util.MapUtils;
import com.tianshouzhi.dragon.ha.exception.DragonHARuntimeException;
import com.tianshouzhi.dragon.ha.jdbc.datasource.RealDataSourceWapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by tianshouzhi on 2017/8/16.
 */
public class RouterManager {
	private final Router readRouter;

	private final Router writeRouter;

	private final Map<String, RealDataSourceWapper> configMap;

	private final Map<String, RealDataSourceWapper> readConfigMap;

	private final Map<String, RealDataSourceWapper> writeConfigMap;

	public RouterManager(Map<String, RealDataSourceWapper> configMap) {
		if (MapUtils.isEmpty(configMap)) {
			throw new DragonHARuntimeException("configMap can't be empty!");
		}
		this.configMap = configMap;
		this.readConfigMap = filterDatasourceConfig(configMap, true);
		this.writeConfigMap = filterDatasourceConfig(configMap, false);
		this.readRouter = buildRouter(readConfigMap, true);
		this.writeRouter = buildRouter(writeConfigMap, false);
	}

	private Router buildRouter(Map<String, RealDataSourceWapper> configMap, boolean isRead) {
		RouteType routeType = getRouteType(configMap);
		switch (routeType) {//fixme
		case SINGLE:
			return new SingleRouter(configMap.keySet().iterator().next());
		case WEIGHT:
			return buildWeightRouter(configMap, isRead);
		default:
			return null;
		}
	}

	private Router buildWeightRouter(Map<String, RealDataSourceWapper> configMap, boolean isRead) {
		HashMap<String, Integer> indexWeightMap = new HashMap<String, Integer>(4);
		for (Map.Entry<String, RealDataSourceWapper> entry : configMap.entrySet()) {
			if (isRead) {
				indexWeightMap.put(entry.getKey(), entry.getValue().getReadWeight());
			} else {
				indexWeightMap.put(entry.getKey(), entry.getValue().getWriteWeight());
			}
		}
		return new WeightRouter(indexWeightMap);
	}

	private RouteType getRouteType(Map<String, RealDataSourceWapper> configMap) {
		if (MapUtils.isEmpty(configMap)) {
			return null;
		}
		if (configMap.size() == 1) {
			return RouteType.SINGLE;
		}
		return RouteType.WEIGHT;
	}

	private Map<String, RealDataSourceWapper> filterDatasourceConfig(Map<String, RealDataSourceWapper> configMap,
																	 boolean isread) {
		Map<String, RealDataSourceWapper> filterResult = new HashMap<String, RealDataSourceWapper>(4);
		for (Map.Entry<String, RealDataSourceWapper> configEntry : configMap.entrySet()) {
			String datasourceIndex = configEntry.getKey();
			RealDataSourceWapper config = configEntry.getValue();
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

	public String routeWrite(Set<String> excludes) {
		if (writeRouter == null) {
			throw new DragonHARuntimeException("writeRouter is null");
		}
		return writeRouter.route(excludes);
	}

	public String routeRead(Set<String> excludes) {
		if (readRouter == null) {
			throw new DragonHARuntimeException("readRouter is null");
		}
		return readRouter.route(excludes);
	}
}
