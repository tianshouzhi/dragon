package com.tianshouzhi.dragon.shard.pipeline.handler.sqlrewrite;

import com.tianshouzhi.dragon.common.jdbc.statement.DragonPrepareStatement;
import com.tianshouzhi.dragon.common.util.StringUtils;
import com.tianshouzhi.dragon.shard.route.LogicTable;

import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * 代表一条sql信息
 */
public class SqlRouteInfo {
	private Statement targetStatement;

	/** 参数位置与参数的映射关系 */
	private Map<Integer, DragonPrepareStatement.ParamSetting> parameters = new HashMap<Integer, DragonPrepareStatement.ParamSetting>();

	/** 真正要执行的sql */
	private String sql;

	private String realDBName;

	// 主维度真实表名
	private String primaryRealTBName;

	/**
	 * 主维度逻辑表
	 */
	private LogicTable primaryLogicTable;

	// 记录这个sql的执行时间
	private long executionTimeMillis;

	public SqlRouteInfo(LogicTable primaryLogicTable, String primaryDBName, String primaryRealTBName) {
		if (StringUtils.isAnyBlank(primaryDBName, primaryRealTBName)) {
			throw new IllegalArgumentException("primaryDBName and primaryRealTBName both can't be blank!!!");
		}
		this.realDBName = primaryDBName;
		this.primaryRealTBName = primaryRealTBName;
		this.primaryLogicTable = primaryLogicTable;
	}

	public void addParam(DragonPrepareStatement.ParamSetting paramSetting) {
		if (parameters == null) {
			parameters = new HashMap<Integer, DragonPrepareStatement.ParamSetting>();
		}
		parameters.put(parameters.size() + 1, paramSetting);
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Map<Integer, DragonPrepareStatement.ParamSetting> getParameters() {
		return parameters;
	}

	public void setParameters(Map<Integer, DragonPrepareStatement.ParamSetting> parameters) {
		this.parameters = parameters;
	}

	public void setTargetStatement(Statement targetStatement) {
		this.targetStatement = targetStatement;
	}

	public Statement getTargetStatement() {
		return targetStatement;
	}

	public String getRealDBName() {
		return realDBName;
	}

	public String getPrimaryRealTBName() {
		return primaryRealTBName;
	}

	public LogicTable getPrimaryLogicTable() {
		return primaryLogicTable;
	}

	public long getExecutionTimeMillis() {
		return executionTimeMillis;
	}

	public void setExecutionTimeMillis(long executionTimeMillis) {
		this.executionTimeMillis = executionTimeMillis;
	}
}
