package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.exception.ExceptionSorter;
import com.tianshouzhi.dragon.common.exception.MySqlExceptionSorter;
import com.tianshouzhi.dragon.common.initailzer.DataSourceUtil;
import com.tianshouzhi.dragon.ha.config.RealDatasourceConfig;
import com.tianshouzhi.dragon.ha.exception.DragonHAException;

import javax.sql.DataSource;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class RealDatasourceWrapper {

	private DataSource realDataSource;

	private boolean isReadOnly;

	private ExceptionSorter exceptionSorter = new MySqlExceptionSorter();

	private RealDatasourceConfig config;

	public RealDatasourceWrapper(RealDatasourceConfig config) throws DragonHAException {
		this.config = config;
		this.isReadOnly = config.getReadWeight() > 0 && config.getWriteWeight() == 0;
		this.realDataSource = refresh(config);
	}

	private DataSource refresh(RealDatasourceConfig config) throws DragonHAException {
		try {
			return DataSourceUtil.create(config.getRealClass(), config.getPropertiesMap());
		} catch (Exception e) {
			throw new DragonHAException("create real datasource '" + config.getIndex() + "' error!", e);
		}
	}

	public synchronized void init() throws DragonHAException {
		try {
			DataSourceUtil.init(realDataSource);
		} catch (Exception e) {
			throw new DragonHAException("init real datasource '" + config.getIndex() + "' error!", e);
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

	public RealDatasourceConfig getConfig() {
		return config;
	}

	public void setConfig(RealDatasourceConfig config) {
		this.config = config;
	}


	public void close() {
		DataSourceUtil.close(realDataSource);
	}
}
