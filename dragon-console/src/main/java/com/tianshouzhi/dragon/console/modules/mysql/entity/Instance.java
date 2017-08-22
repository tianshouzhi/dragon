package com.tianshouzhi.dragon.console.modules.mysql.entity;

import com.tianshouzhi.dragon.console.modules.mysql.entity.enumeration.InstanceRole;
import com.tianshouzhi.dragon.console.modules.mysql.entity.enumeration.InstanceStatus;

import java.util.Date;

/**
 * Created by tianshouzhi on 2017/8/18.
 */
public class Instance {
	private Long id;

	private Long clusterId;

	private String ip;

	private Integer port;

	private InstanceStatus status;

	private InstanceRole role;

	private Date gmtCreate;

	private Date gmtUpdate;

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

	public InstanceRole getRole() {
		return role;
	}

	public void setRole(InstanceRole role) {
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
		return "Instance{" + "id=" + id + ", clusterId=" + clusterId + ", ip='" + ip + '\'' + ", port=" + port
		      + ", status=" + status + ", role=" + role + ", gmtCreate=" + gmtCreate + ", gmtUpdate=" + gmtUpdate + '}';
	}
}
