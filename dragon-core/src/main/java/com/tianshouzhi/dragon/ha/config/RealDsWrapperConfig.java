package com.tianshouzhi.dragon.ha.config;

import java.util.Properties;

/**
 * Created by tianshouzhi on 2017/10/13.
 */
public class RealDsWrapperConfig {
	private String realDsName;

	private int readWeight;

	private int writeWeight;

	private String realDsClass;

	private Properties realDsProperties;

	public String getRealDsName() {
		return realDsName;
	}

	public void setRealDsName(String realDsName) {
		this.realDsName = realDsName;
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
