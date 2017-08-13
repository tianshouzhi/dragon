package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.exception.DragonRuntimeException;
import com.tianshouzhi.dragon.common.exception.ExceptionSorter;
import com.tianshouzhi.dragon.common.exception.MySqlExceptionSorter;
import com.tianshouzhi.dragon.common.initailzer.DataSourceUtil;
import com.tianshouzhi.dragon.common.log.Log;
import com.tianshouzhi.dragon.common.log.LoggerFactory;
import com.tianshouzhi.dragon.ha.config.RealDatasourceConfig;
import com.tianshouzhi.dragon.ha.exception.DragonHAException;

import javax.sql.DataSource;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class RealDatasourceWrapper {
	private static final Log LOGGER = LoggerFactory.getLogger(RealDatasourceWrapper.class);
	private DataSource realDataSource;
	private volatile boolean  init=false;
	private boolean isReadOnly;

	private ExceptionSorter exceptionSorter = new MySqlExceptionSorter();

	private RealDatasourceConfig config;

	public RealDatasourceWrapper(RealDatasourceConfig config) throws DragonHAException {
		this.config = config;
		this.isReadOnly = config.getReadWeight() > 0 && config.getWriteWeight() == 0;
	}

	private DataSource createRealDataSource(RealDatasourceConfig config) throws DragonHAException {
		try {
			return DataSourceUtil.create(config.getRealClass(), config.getPropertiesMap());
		} catch (Exception e) {
			throw new DragonHAException("create real datasource '" + config.getIndex() + "' error!", e);
		}
	}

	public synchronized void init() throws DragonHAException {
		if(init){
			return;
		}
		try {
			this.realDataSource = createRealDataSource(this.config);
			DataSourceUtil.init(this.realDataSource );
			init=true;
			LOGGER.info("init real datasource '"+config.getIndex()+"' success!");
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

	public DataSource getRealDataSource(){
		if(this.realDataSource==null){
			try {
				init();
			} catch (DragonHAException e) {
				throw new DragonRuntimeException(e);
			}
		}
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
		LOGGER.info("close real datasource '"+config.getIndex()+"'!!!");
		DataSourceUtil.close(realDataSource);
	}
}
