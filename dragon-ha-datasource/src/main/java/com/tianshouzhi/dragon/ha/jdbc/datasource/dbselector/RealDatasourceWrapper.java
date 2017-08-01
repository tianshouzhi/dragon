package com.tianshouzhi.dragon.ha.jdbc.datasource.dbselector;

import com.tianshouzhi.dragon.common.exception.DragonException;
import com.tianshouzhi.dragon.common.exception.ExceptionSorter;
import com.tianshouzhi.dragon.common.exception.MySqlExceptionSorter;
import com.tianshouzhi.dragon.common.initailzer.DataSourceUtil;
import com.tianshouzhi.dragon.ha.config.RealDatasourceConfig;
import org.apache.commons.lang3.StringUtils;

import javax.sql.CommonDataSource;
import javax.sql.DataSource;
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
		if (config == null) {
			throw new NullPointerException();
		}
		check(config.getIndex(), config.getReadWeight(), config.getWriteWeight());
		this.config = config;
		this.isReadOnly = config.getReadWeight() > 0 && config.getWriteWeight() == 0;
		this.realDataSource=createRealDatasource(config);
	}

	private DataSource createRealDatasource(RealDatasourceConfig config) throws DragonException {
		List<RealDatasourceConfig.Property> properties = config.getProperties();
		try {
			return DataSourceUtil.create(config.getRealClass(),RealDatasourceConfig.propertiesToMap(properties));
		} catch (Exception e) {
			throw new DragonException("init datasource '" + config.getIndex() + "' error!", e);
		}
	}

	public synchronized void init() throws DragonException {
		try {
			DataSourceUtil.init(realDataSource);
		} catch (Exception e) {
			throw new DragonException("init datasource '" + config.getIndex() + "' error!", e);
		}
	}

	private void check(String dataSourceIndex, int readWeight, int writeWeight) {
		if (StringUtils.isBlank(dataSourceIndex)) {
			throw new IllegalArgumentException("parameter 'dataSourceIndex' can't be empty or blank");
		}
		if (readWeight < 0 || writeWeight < 0 || (readWeight == writeWeight && readWeight == 0)) {
			throw new IllegalArgumentException(
			      "either 'readWeight' or 'writeWeight' can't less than zero,and can't be zero at the same time,current readWeight:"
			            + readWeight + ",current writeWeight:" + writeWeight);
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

	@Override
	public String toString() {
		return "RealDatasourceWrapper{" + ", readWeight=" + config.getReadWeight() + ", writeWeight="
		      + config.getWriteWeight() + ", realDataSource=" + realDataSource.getClass().getName() + '}';
	}
}
