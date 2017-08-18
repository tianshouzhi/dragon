package com.tianshouzhi.dragon.real;

import javax.sql.DataSource;

/**
 * Created by tianshouzhi on 2017/8/17.
 */
public interface DragonRealDataSource extends DataSource{
	public void init() throws Throwable;

	public void close() throws Throwable;
}
