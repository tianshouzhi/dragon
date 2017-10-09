package com.tianshouzhi.dragon.console.modules.datasource_config.entity;

import com.tianshouzhi.dragon.console.base.BaseEntity;

/**
 * Created by tianshouzhi on 2017/9/5.
 */
public class HADataSourceMapping extends BaseEntity {

	private Long realDsId;

	private Long haDsId;

	private Long readWeight;

	private Long writeWeight;

	public Long getRealDsId() {
		return realDsId;
	}

	public void setRealDsId(Long realDsId) {
		this.realDsId = realDsId;
	}

	public Long getHaDsId() {
		return haDsId;
	}

	public void setHaDsId(Long haDsId) {
		this.haDsId = haDsId;
	}

	public Long getReadWeight() {
		return readWeight;
	}

	public void setReadWeight(Long readWeight) {
		this.readWeight = readWeight;
	}

	public Long getWriteWeight() {
		return writeWeight;
	}

	public void setWriteWeight(Long writeWeight) {
		this.writeWeight = writeWeight;
	}
}
