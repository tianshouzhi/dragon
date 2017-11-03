package com.tianshouzhi.dragon.sharding.jdbc.datasource;

import com.tianshouzhi.dragon.common.exception.DragonException;
import com.tianshouzhi.dragon.common.thread.DragonThreadFactory;
import com.tianshouzhi.dragon.common.util.CollectionUtils;
import com.tianshouzhi.dragon.common.util.MapUtils;
import com.tianshouzhi.dragon.common.util.StringUtils;
import com.tianshouzhi.dragon.sharding.route.LogicDatasource;
import com.tianshouzhi.dragon.sharding.route.LogicTable;

import javax.sql.DataSource;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by TIANSHOUZHI336 on 2017/3/15.
 */
public abstract class DragonShardingConfigParser {
	// 如果需要进行监控的话，必须传入appName
	public static String parseAppName(Properties properties) {
		String appName = properties.getProperty("dragon.appName");
		/*
		 * if(StringUtils.isBlank(appName)){ throw new DragonException("dragon.appName can't be null"); }
		 */
		return appName;
	}

	public static LogicDatasource parseLogicDatasouce(Properties properties) throws Exception {
		String dsNamePattern = properties.getProperty("datasource.namePattern");
		String realDatasourceClass = properties.getProperty("datasource.datasourceClass");
		Map<String, String> defaultDatasourceConfigMap = parseDeafultDatasourceConfig(properties);
		HashMap<String, Map<String, String>> datasouceConfigMap = parseEachDatasouceConfigMap(properties);
		Map<String, DataSource> dsNameDatasourceMap = new HashMap<String, DataSource>();
		for (Map.Entry<String, Map<String, String>> entry : datasouceConfigMap.entrySet()) {
			String datasourceName = entry.getKey();
			Map<String, String> datasourceConfig = entry.getValue();
			Map<String, String> mergedConfig = new HashMap<String, String>();
			mergedConfig.putAll(defaultDatasourceConfigMap);
			mergedConfig.putAll(datasourceConfig);// 覆盖默认配置
//			DataSource dataSource = DataSourceUtil.create(realDatasourceClass, mergedConfig);
			DataSource dataSource = null;
			dsNameDatasourceMap.put(datasourceName, dataSource);
		}

		String defaultDSName = properties.getProperty("datasource.defaultDSName");
		LogicDatasource logicDatasource = new LogicDatasource(dsNamePattern, dsNameDatasourceMap, defaultDSName);

		return logicDatasource;
	}

	// 解析数据源的默认配置
	private static Map<String, String> parseDeafultDatasourceConfig(Properties properties) {
		Map<String, String> defaultDatasourceConfigMap = new HashMap<String, String>();
		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			if (key.startsWith("datasource.default.")) {
				String dsProperty = key.substring("datasource.default.".length());
				defaultDatasourceConfigMap.put(dsProperty, value);
				continue;
			}
		}
		return defaultDatasourceConfigMap;
	}

	// 解析每一个数据源的配置
	private static HashMap<String, Map<String, String>> parseEachDatasouceConfigMap(Properties properties) {
		String datasoureNames = properties.getProperty("datasource.list");
		HashMap<String, Map<String, String>> datasouceConfigMap = new HashMap<String, Map<String, String>>();
		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			for (String datasourceName : datasoureNames.split(",")) {
				if (key.startsWith("datasource." + datasourceName + ".")) {
					Map<String, String> eachDSConfigMap = datasouceConfigMap.get(datasourceName);
					if (eachDSConfigMap == null) {
						eachDSConfigMap = new HashMap<String, String>();
						datasouceConfigMap.put(datasourceName, eachDSConfigMap);
					}
					String dsProperty = key.substring(("datasource." + datasourceName + ".").length());
					eachDSConfigMap.put(dsProperty, value);
					continue;
				}
			}
		}
		return datasouceConfigMap;
	}

	public static Map<String, LogicTable> parseLogicTableMap(LogicDatasource logicDatasource, Properties properties)
	      throws DragonException {
		Map<String, LogicTable> result = new HashMap<String, LogicTable>();
		String logicTableNames = properties.getProperty("logicTable.list");
		if (StringUtils.isBlank(logicTableNames)) {
			throw new DragonException("logicTable.list can't be null");
		}

		LogicTableConfig defaultLogicTableConfig = parseLogicTableConfig("default", logicDatasource, properties);
		for (String logicTableName : logicTableNames.split(",")) {
			LogicTableConfig logicTableConfig = parseLogicTableConfig(logicTableName, logicDatasource, properties);
			result.put(logicTableName,
			      makeLogicTable(logicTableName, defaultLogicTableConfig, logicTableConfig, logicDatasource));
		}
		return result;
	}

	private static LogicTableConfig parseLogicTableConfig(String logicTableName, LogicDatasource logicDatasource,
	      Properties properties) {
		LogicTableConfig logicTableConfig = new LogicTableConfig();
		logicTableConfig.tbNamePattern = properties.getProperty("logicTable." + logicTableName + ".namePattern");
		String dbRouteRules = properties.getProperty("logicTable." + logicTableName + ".dbRouteRules");
		if (StringUtils.isNotBlank(dbRouteRules)) {
			logicTableConfig.dbRouteRules = Arrays.asList(dbRouteRules.split(","));
		}
		String tbRouteRules = properties.getProperty("logicTable." + logicTableName + ".tbRouteRules");
		if (StringUtils.isNotBlank(tbRouteRules)) {
			logicTableConfig.tbRouteRules = Arrays.asList(tbRouteRules.split(","));
		}
		String everydbMappingStr = properties.getProperty("logicTable." + logicTableName + ".everydb.mapping");
		Set<String> datasourceNames = logicDatasource.getRealDbIndexDatasourceMap().keySet();
		// 所有库级别的默认映射规则
		HashMap<String, String> realDbTbMapping = new HashMap<String, String>();
		if (StringUtils.isNotBlank(everydbMappingStr)) {
			for (String datasourceName : datasourceNames) {
				realDbTbMapping.put(datasourceName, everydbMappingStr);
			}
		}
		// 单个库的默认映射规则，如果存在则覆盖所有库的默认配置
		for (String datasourceName : datasourceNames) {
			String eachDatasourceMapping = properties
			      .getProperty("logicTable." + logicTableName + "." + datasourceName + ".mapping");
			if (StringUtils.isNotBlank(eachDatasourceMapping)) {
				realDbTbMapping.put(datasourceName, eachDatasourceMapping);
			}
		}
		logicTableConfig.realDbTbMapping = realDbTbMapping;
		return logicTableConfig;
	}

	private static LogicTable makeLogicTable(String logicTbName, LogicTableConfig defaultLogicTableConfig,
	      LogicTableConfig logicTableConfig, LogicDatasource logicDatasource) throws DragonException {
		List<String> defaultDbRouteRules = defaultLogicTableConfig.dbRouteRules;
		Map<String, String> defaultRealDbTbMapping = defaultLogicTableConfig.realDbTbMapping;
		String defaultTbNameFormat = defaultLogicTableConfig.tbNamePattern;
		List<String> defaultTbRouteRules = defaultLogicTableConfig.tbRouteRules;

		String tbNameFormat = logicTableConfig.tbNamePattern;
		if (StringUtils.isBlank(tbNameFormat)) {
			tbNameFormat = defaultTbNameFormat.replaceAll("#logicTable#", logicTbName);
		}

		Set<String> dbRouteRules = new HashSet<String>();
		if (CollectionUtils.isNotEmpty(logicTableConfig.dbRouteRules)) {
			dbRouteRules.addAll(logicTableConfig.dbRouteRules);
		} else if (CollectionUtils.isNotEmpty(defaultDbRouteRules)) {
			dbRouteRules.addAll(defaultDbRouteRules);
		}
		if (dbRouteRules == null) {
			throw new DragonException("no default dbRouteRules config ,'" + logicTbName + "'must config dbRouteRules");
		}

		Set<String> tbRouteRules = new HashSet<String>();
		if (CollectionUtils.isNotEmpty(logicTableConfig.tbRouteRules)) {
			tbRouteRules.addAll(logicTableConfig.tbRouteRules);
		} else if (CollectionUtils.isNotEmpty(defaultTbRouteRules)) {
			tbRouteRules.addAll(defaultTbRouteRules);
		}
		if (tbRouteRules == null) {
			throw new DragonException("no default tbRouteRules config ,'" + logicTbName + "'must config tbRouteRules");
		}

		Map<String, List<String>> realDbTbMapping = null;

		MessageFormat messageFomart = new MessageFormat(tbNameFormat);
		if (MapUtils.isNotEmpty(logicTableConfig.realDbTbMapping)) {
			realDbTbMapping = caculateRealDBTBMapping(logicTableConfig.realDbTbMapping, messageFomart);
		} else if (MapUtils.isNotEmpty(defaultRealDbTbMapping)) {
			realDbTbMapping = caculateRealDBTBMapping(defaultRealDbTbMapping, messageFomart);
		}
		// 如果要支持sql中不指定路由字段，则必须指定realDbTbMapping
		/*
		 * if (realDbTbMapping == null) { throw new
		 * DragonException("no default realDbTbMapping config ,logic table '"+logicTbName+"' must config realDbTbMapping"); }
		 */

		return new LogicTable(logicTbName, tbNameFormat, tbRouteRules, dbRouteRules, logicDatasource, realDbTbMapping);
	}

	private static Map<String, List<String>> caculateRealDBTBMapping(Map<String, String> defaultRealDbTbMapping,
	      MessageFormat messageFomart) {
		Map<String, List<String>> realDbTbMapping = new HashMap<String, List<String>>();
		for (Map.Entry<String, String> entry : defaultRealDbTbMapping.entrySet()) {
			String realDBName = entry.getKey();
			String realTBRangeStr = entry.getValue();
			String[] tbIndexRange = realTBRangeStr
			      .substring(realTBRangeStr.indexOf("[") + 1, realTBRangeStr.lastIndexOf("]")).split(",");
			int start = Integer.parseInt(tbIndexRange[0]);
			int end = Integer.parseInt(tbIndexRange[1]);
			List<String> list = new ArrayList<String>();
			for (int i = start; i <= end; i++) {
				String realTBName = messageFomart.format(new Object[] { i });
				list.add(realTBName);
			}
			realDbTbMapping.put(realDBName, list);
		}
		return realDbTbMapping;
	}

	public static int parseExecutionTimeout(Properties properties) {
		int timeout = 3000;
		if (properties.getProperty("parallel.execution.timeout") != null) {
			timeout = Integer.parseInt(properties.getProperty("parallel.execution.timeout"));
		}
		return timeout;
	}

	public static ExecutorService makeExecutorService(String appName, LogicDatasource logicDatasource,
	      Map<String, LogicTable> logicTableMap, Properties properties) {
		int corePoolSize = logicDatasource.getRealDbIndexDatasourceMap().size();
		if (properties.getProperty("dragon.executor.corePoolSize") != null) {
			corePoolSize = Integer.parseInt(properties.getProperty("dragon.executor.corePoolSize"));
		}
		int maxPoolSize = logicDatasource.getRealDbIndexDatasourceMap().size();
		if (properties.getProperty("dragon.executor.maxPoolSize") != null) {
			maxPoolSize = Integer.parseInt(properties.getProperty("dragon.executor.maxPoolSize"));
		}
		int workQueueSize = 1000;
		if (properties.getProperty("dragon.executor.workQueueSize") != null) {
			workQueueSize = Integer.parseInt(properties.getProperty("dragon.executor.workQueueSize"));
		}

		return new ThreadPoolExecutor(corePoolSize, maxPoolSize, 3, TimeUnit.MINUTES,
		      new LinkedBlockingQueue<Runnable>(workQueueSize),
		      new DragonThreadFactory("dragon-sharding-" + appName + "-pool"));
	}

	/**
	 * Created by TIANSHOUZHI336 on 2017/3/20.
	 */
	private static class LogicTableConfig {
		String tbNamePattern;

		List<String> dbRouteRules;

		List<String> tbRouteRules;

		Map<String, String> realDbTbMapping;// ${logic_table_name}_[0000,0001]
	}
}
