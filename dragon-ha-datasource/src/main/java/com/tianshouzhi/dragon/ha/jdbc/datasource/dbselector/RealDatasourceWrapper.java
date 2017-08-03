package com.tianshouzhi.dragon.ha.jdbc.datasource.dbselector;

import com.tianshouzhi.dragon.common.exception.DragonException;
import com.tianshouzhi.dragon.common.exception.ExceptionSorter;
import com.tianshouzhi.dragon.common.exception.MySqlExceptionSorter;
import com.tianshouzhi.dragon.common.initailzer.DataSourceUtil;
import com.tianshouzhi.dragon.ha.config.RealDatasourceConfig;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class RealDatasourceWrapper {

	private DataSource realDataSource;

	private boolean isReadOnly;

	private ExceptionSorter exceptionSorter = new MySqlExceptionSorter();

	private RealDatasourceConfig config;

	public RealDatasourceWrapper(RealDatasourceConfig config) throws DragonException {
		check(config);
		this.config = config;
		this.isReadOnly = config.getReadWeight() > 0 && config.getWriteWeight() == 0;
		this.realDataSource = createRealDatasource(config);
	}

	private DataSource createRealDatasource(RealDatasourceConfig config) throws DragonException {
		try {
			return DataSourceUtil.create(config.getRealClass(), config.getPropertiesMap());
		} catch (Exception e) {
			throw new DragonException("create datasource '" + config.getIndex() + "' error!", e);
		}
	}

	public synchronized void init() throws DragonException {
		try {
			DataSourceUtil.init(realDataSource);
		} catch (Exception e) {
			throw new DragonException("init datasource '" + config.getIndex() + "' error!", e);
		}
	}

	private void check(RealDatasourceConfig config) {
		if (config == null) {
			throw new NullPointerException();
		}

		String index = config.getIndex();

		if (StringUtils.isBlank(index)) {
			throw new IllegalArgumentException("parameter 'dataSourceIndex' can't be empty or blank");
		}

		Integer readWeight = config.getReadWeight();
		Integer writeWeight = config.getWriteWeight();

		if (readWeight < 0 || writeWeight < 0 ) {
			throw new IllegalArgumentException(
					"'"+ index + "' config error, both 'readWeight' and 'writeWeight' can't less than zero," +
						  "current readWeight:"+ readWeight + ",current writeWeight:" + writeWeight);
		}

		try {
			DataSourceUtil.checkConfig(config.getRealClass(), config.getPropertiesMap());
		} catch (SQLException e) {
			throw new IllegalArgumentException("config error ,please check【"+config+"】",e);
		}

	}

	public int getReadWeight() {
		return config.getReadWeight();
	}

	public int getWriteWeight() {
		return config.getWriteWeight();
	}

	public DataSource getRealDataSource() {
		return realDataSource;
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}

	public ExceptionSorter getExceptionSorter() {
		return exceptionSorter;
	}
}
