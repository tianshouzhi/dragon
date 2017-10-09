package com.tianshouzhi.dragon.real.jdbc;

import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSource;

import javax.sql.DataSource;

/**
 * Created by tianshouzhi on 2017/9/12.
 */
public abstract class RealDataSource<T extends DataSource> extends DragonDataSource {
	protected String index;

	protected int readWeight = 10;

	protected int writeWeight = 10;

	protected T dataSource;

	public RealDataSource(String index, int readWeight, int writeWeight, T dataSource) {
		this.index = index;
		this.readWeight = readWeight;
		this.writeWeight = writeWeight;
		this.dataSource = dataSource;
	}

	// getters and setters
	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public Integer getReadWeight() {
		return readWeight;
	}

	public void setReadWeight(Integer readWeight) {
		this.readWeight = readWeight;
	}

	public Integer getWriteWeight() {
		return writeWeight;
	}

	public void setWriteWeight(Integer writeWeight) {
		this.writeWeight = writeWeight;
	}

	public T getDataSource() {
		return dataSource;
	}

	public void setDataSource(T dataSource) {
		this.dataSource = dataSource;
	}

	public boolean canRead() {
		return readWeight > 0;
	}

	public boolean canWrite() {
		return writeWeight > 0;
	}

	public void setReadWeight(int readWeight) {
		this.readWeight = readWeight;
	}

	public void setWriteWeight(int writeWeight) {
		this.writeWeight = writeWeight;
	}
}
