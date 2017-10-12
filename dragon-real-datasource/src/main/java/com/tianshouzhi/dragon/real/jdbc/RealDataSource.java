package com.tianshouzhi.dragon.real.jdbc;

import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSource;

import javax.sql.DataSource;

/**
 * Created by tianshouzhi on 2017/9/12.
 */
public abstract class RealDataSource<T extends DataSource> extends DragonDataSource {
	protected String index;
	protected T dataSource;

	public RealDataSource(String index,T dataSource) {
		this.index = index;
		this.dataSource = dataSource;
	}

	// getters and setters
	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public T getDataSource() {
		return dataSource;
	}

	public void setDataSource(T dataSource) {
		this.dataSource = dataSource;
	}
}
