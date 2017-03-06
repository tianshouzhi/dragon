package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite;

import com.tianshouzhi.dragon.common.jdbc.statement.DragonPrepareStatement;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2017/2/28.
 */
public class SqlRouteInfo {
    private String dbName;
    private String tableName;
    private PreparedStatement targetStatement;
    private Map<Integer, DragonPrepareStatement.ParamSetting> parameters=new HashMap<Integer, DragonPrepareStatement.ParamSetting>();
    private StringBuilder sql;

    public SqlRouteInfo(String dbName,String tableName) {
        this.dbName = dbName;
        this.tableName = tableName;
    }

    public void addParam(DragonPrepareStatement.ParamSetting paramSetting) {
        if(parameters==null){
            parameters=new HashMap<Integer, DragonPrepareStatement.ParamSetting>();
        }
        parameters.put(parameters.size()+1,paramSetting);
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public StringBuilder getSql() {
        return sql;
    }

    public void setSql(StringBuilder sql) {
        this.sql = sql;
    }

    public Map<Integer, DragonPrepareStatement.ParamSetting> getParameters() {
        return parameters;
    }

    public void setParameters(Map<Integer, DragonPrepareStatement.ParamSetting> parameters) {
        this.parameters = parameters;
    }

    public void setTargetStatement(PreparedStatement targetStatement) {
        this.targetStatement = targetStatement;
    }

    public PreparedStatement getTargetStatement() {
        return targetStatement;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

}
