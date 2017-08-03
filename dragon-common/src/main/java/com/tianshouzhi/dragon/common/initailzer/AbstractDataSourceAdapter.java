package com.tianshouzhi.dragon.common.initailzer;

import com.tianshouzhi.dragon.common.exception.DragonException;
import org.apache.commons.beanutils.BeanUtils;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2017/3/21.
 */
public abstract class AbstractDataSourceAdapter implements DataSourceAdapter {

	public DataSource create(Map<String, String> config) throws Exception {
		String datasouceClassName = getClassName();
		Class<?> clazz = Class.forName(datasouceClassName);
		DataSource dataSource = (DataSource) clazz.newInstance();
		BeanUtils.copyProperties(dataSource, config);
		return dataSource;
	}

	public void init(DataSource dataSource) throws Exception {
		try {
			Method initMethod = dataSource.getClass().getMethod("init");
			initMethod.invoke(dataSource);
		} catch (NoSuchMethodException ignore) {
		}
	}

	public void close(DataSource dataSource) {
		try {
			Method initMethod = dataSource.getClass().getMethod("close");
			initMethod.invoke(dataSource);
		} catch (Exception ignore) {

		}
	}

	@Override
	public void checkConfig(Map<String, String> config) throws SQLException {
		String url = config.get("url");
		String username = config.get("username");
		String password = config.get("password");
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection connection = DriverManager.getConnection(url, username, password);

	}
}
