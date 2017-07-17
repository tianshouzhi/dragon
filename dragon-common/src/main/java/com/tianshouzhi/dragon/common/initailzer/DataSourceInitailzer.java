package com.tianshouzhi.dragon.common.initailzer;

import com.tianshouzhi.dragon.common.exception.DragonException;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2017/3/20.
 */
public interface DataSourceInitailzer {
	/**
	 * 返回初始化的数据源类名
	 * 
	 * @return
	 */
	public String initDatasouceClassName();

	public DataSource init(Map<String, String> config) throws DragonException;
}
