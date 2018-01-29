package com.tianshouzhi.dragon.web.modules.cluster.entity;

import com.tianshouzhi.dragon.web.modules.cluster.entity.enumeration.InstanceStatus;
import com.tianshouzhi.dragon.web.common.BaseEntity;

/**
 * 集群实例
 * Created by tianshouzhi on 2017/8/18.
 */
public class Instance extends BaseEntity{

	private Long clusterId;

	private String ip;

	private Integer port;

	private InstanceStatus status;

	private Boolean isMaster;

	public Long getClusterId() {
		return clusterId;
	}

	public void setClusterId(Long clusterId) {
		this.clusterId = clusterId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public InstanceStatus getStatus() {
		return status;
	}

	public void setStatus(InstanceStatus status) {
		this.status = status;
	}

	public Boolean getMaster() {
		return isMaster;
	}

	public void setMaster(Boolean master) {
		isMaster = master;
	}

	@Override
	public String toString() {
		return "Instance{" + "id=" + id + ", clusterId=" + clusterId + ", ip='" + ip + '\'' + ", port=" + port
		      + ", status=" + status + ", isMaster=" + isMaster + ", gmtCreate=" + gmtCreate + ", gmtUpdate=" + gmtUpdate + '}';
	}
}
