package com.tianshouzhi.dragon.common.initailzer;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Created by TIANSHOUZHI336 on 2017/3/20.
 */
public abstract class DataSourceUtil {
	private static ServiceLoader<DataSourceAdapter> serviceLoader = ServiceLoader.load(DataSourceAdapter.class);;

	private static Map<String, DataSourceAdapter> classNameInitailzerMap = new HashMap<String, DataSourceAdapter>();
	static {
		Iterator<DataSourceAdapter> iterator = serviceLoader.iterator();
		classNameInitailzerMap = new HashMap<String, DataSourceAdapter>();
		while (iterator.hasNext()) {
			DataSourceAdapter dataSourceAdapter = iterator.next();
			String className = dataSourceAdapter.getClassName();
			classNameInitailzerMap.put(className, dataSourceAdapter);
		}
	}

	public static DataSource create(String datasourceClass, Map<String, String> config) throws Exception {
		return getDatasourceAdapter(datasourceClass).create(config);
	}

	public static void init(DataSource dataSource) throws Exception {
		getDatasourceAdapter(dataSource.getClass().getName()).init(dataSource);
	}

	public static void close(DataSource dataSource) {
		getDatasourceAdapter(dataSource.getClass().getName()).close(dataSource);
	}

	public static void checkConnection(String datasourceClass, Map<String, String> config) throws SQLException{
		getDatasourceAdapter(datasourceClass).checkConfig(config);
	}

	private static DataSourceAdapter getDatasourceAdapter(String datasourceClass) {
		DataSourceAdapter dataSourceAdapter = classNameInitailzerMap.get(datasourceClass);
		if (dataSourceAdapter == null) {
			throw new NullPointerException("can't init datasource type:" + datasourceClass
					+ ",you should custom a DataSourceAdapter and add in the classpath");
		}
		return dataSourceAdapter;
	}
}
