package com.tianshouzhi.dragon.web.modules.datasource.entity;

import com.tianshouzhi.dragon.web.common.BaseEntity;

/**
 * Created by tianshouzhi on 2017/9/5.
 */
public class ShardRule extends BaseEntity {

	private Long shardDsId;

	private String logicTable;

	private String dbRouteRule;

	private String tbRouteRule;

	private Boolean needBroadCast = false;// 是否需要小表广播

	public Long getShardDsId() {
		return shardDsId;
	}

	public void setShardDsId(Long shardDsId) {
		this.shardDsId = shardDsId;
	}

	public String getLogicTable() {
		return logicTable;
	}

	public void setLogicTable(String logicTable) {
		this.logicTable = logicTable;
	}

	public String getDbRouteRule() {
		return dbRouteRule;
	}

	public void setDbRouteRule(String dbRouteRule) {
		this.dbRouteRule = dbRouteRule;
	}

	public String getTbRouteRule() {
		return tbRouteRule;
	}

	public void setTbRouteRule(String tbRouteRule) {
		this.tbRouteRule = tbRouteRule;
	}

	public Boolean getNeedBroadCast() {
		return needBroadCast;
	}

	public void setNeedBroadCast(Boolean needBroadCast) {
		this.needBroadCast = needBroadCast;
	}
}
