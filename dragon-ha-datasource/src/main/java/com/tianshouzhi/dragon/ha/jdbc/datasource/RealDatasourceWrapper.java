package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.exception.DragonException;
import com.tianshouzhi.dragon.common.exception.ExceptionSorter;
import com.tianshouzhi.dragon.common.exception.MySqlExceptionSorter;
import com.tianshouzhi.dragon.common.initailzer.DataSourceUtil;
import com.tianshouzhi.dragon.ha.config.RealDatasourceConfig;

import javax.sql.DataSource;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class RealDatasourceWrapper {

	private DataSource realDataSource;

	private boolean isReadOnly;

	private ExceptionSorter exceptionSorter = new MySqlExceptionSorter();

	private RealDatasourceConfig config;

	public RealDatasourceWrapper(RealDatasourceConfig config) throws DragonException {
		this.config = config;
		this.isReadOnly = config.getReadWeight() > 0 && config.getWriteWeight() == 0;
		this.realDataSource = refresh(config);
	}

	private DataSource refresh(RealDatasourceConfig config) throws DragonException {
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
