package com.tianshouzhi.dragon.common.jdbc.datasource;

import javax.sql.DataSource;

/**
 * Created by tianshouzhi on 2017/10/13.
 */
public interface DragonDataSource extends DataSource {
	void init() throws Exception;

	void close() throws Exception;
}
