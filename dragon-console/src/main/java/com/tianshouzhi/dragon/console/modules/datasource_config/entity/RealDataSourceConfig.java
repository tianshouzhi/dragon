package com.tianshouzhi.dragon.console.modules.datasource_config.entity;

import java.util.Date;

/**
 * Created by tianshouzhi on 2017/8/18.
 */
public class RealDataSourceConfig {
	private Long id;

	private Long haId;

	private Long shardId;

	private String config;

	private Date gmtCreate;

	private Date gmtUpdate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getHaId() {
		return haId;
	}

	public void setHaId(Long haId) {
		this.haId = haId;
	}

	public Long getShardId() {
		return shardId;
	}

	public void setShardId(Long shardId) {
		this.shardId = shardId;
	}

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtUpdate() {
		return gmtUpdate;
	}

	public void setGmtUpdate(Date gmtUpdate) {
		this.gmtUpdate = gmtUpdate;
	}

    @Override
    public String toString() {
        return "RealDataSourceConfig{" +
                "id=" + id +
                ", haId=" + haId +
                ", shardId=" + shardId +
                ", config='" + config + '\'' +
                ", gmtCreate=" + gmtCreate +
                ", gmtUpdate=" + gmtUpdate +
                '}';
    }
}
