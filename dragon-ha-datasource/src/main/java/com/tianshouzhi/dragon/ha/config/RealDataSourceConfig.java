package com.tianshouzhi.dragon.ha.config;

import java.util.Properties;

/**
 * Created by tianshouzhi on 2017/10/13.
 */
public class RealDataSourceConfig {
	private String index;

	private int readWeight;

	private int writeWeight;

	private String realDsClass;

	private Properties realDsProperties;

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public int getReadWeight() {
		return readWeight;
	}

	public void setReadWeight(int readWeight) {
		this.readWeight = readWeight;
	}

	public int getWriteWeight() {
		return writeWeight;
	}

	public void setWriteWeight(int writeWeight) {
		this.writeWeight = writeWeight;
	}

	public String getRealDsClass() {
		return realDsClass;
	}

	public void setRealDsClass(String realDsClass) {
		this.realDsClass = realDsClass;
	}

	public Properties getRealDsProperties() {
		return realDsProperties;
	}

	public void setRealDsProperties(Properties realDsProperties) {
		this.realDsProperties = realDsProperties;
	}
}
