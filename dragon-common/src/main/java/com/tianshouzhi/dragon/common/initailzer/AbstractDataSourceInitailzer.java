package com.tianshouzhi.dragon.common.initailzer;

import com.tianshouzhi.dragon.common.exception.DragonException;
import org.apache.commons.beanutils.BeanUtils;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2017/3/21.
 */
public abstract class AbstractDataSourceInitailzer implements DataSourceInitailzer {
	@Override
	public DataSource init(Map<String, String> config) throws DragonException {
		try {
			String datasouceClassName = initDatasouceClassName();
			Class<?> clazz = Class.forName(datasouceClassName);
			DataSource dataSource = (DataSource) clazz.newInstance();
			BeanUtils.copyProperties(dataSource, config);
			return dataSource;
		} catch (Exception e) {
			throw new DragonException("innit real datasource error", e);
		}
	}
}
