package com.tianshouzhi.dragon.common.initailzer;

import com.tianshouzhi.dragon.common.exception.DragonException;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2017/3/20.
 */
public interface DataSourceAdapter {
	/**
	 * 返回初始化的数据源类名
	 * @return
	 */
	public String getClassName();

	public DataSource create(Map<String, String> config) throws Exception;

	public void init(DataSource dataSource) throws Exception;

	public void close(DataSource dataSource) throws Exception;
}
