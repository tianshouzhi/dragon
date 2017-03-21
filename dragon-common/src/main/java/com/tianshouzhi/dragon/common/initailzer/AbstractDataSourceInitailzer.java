package com.tianshouzhi.dragon.common.initailzer;

import org.apache.commons.beanutils.BeanUtils;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2017/3/21.
 */
public abstract class AbstractDataSourceInitailzer implements DataSourceInitailzer{
    @Override
    public DataSource init(Map<String, String> config) throws Exception {
        String datasouceClassName = initDatasouceClassName();
        Class<?> clazz = Class.forName(datasouceClassName);
        DataSource dataSource = (DataSource) clazz.newInstance();
        BeanUtils.copyProperties(dataSource,config);
        return dataSource;
    }
}
