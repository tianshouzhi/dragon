package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite;

import com.tianshouzhi.dragon.common.jdbc.statement.DragonPrepareStatement;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

/**
 * 代表一条sql信息
 */
public class SqlRouteInfo {
    private PreparedStatement targetStatement;
    /**参数位置与参数的映射关系*/
    private Map<Integer, DragonPrepareStatement.ParamSetting> parameters=new HashMap<Integer, DragonPrepareStatement.ParamSetting>();

    /**真正要执行的sql*/
    private String sql;

    private String realDBName;
    //主维度真实表名
    private String primaryTBName;
    //主维度表的tb Index
    private Long primaryTBIndex;

    public SqlRouteInfo( String realDBName, Long primaryTBIndex,String primaryTBName) {
        this.primaryTBIndex = primaryTBIndex;
        this.realDBName=realDBName;
        this.primaryTBName=primaryTBName;
    }

    public void addParam(DragonPrepareStatement.ParamSetting paramSetting) {
        if(parameters==null){
            parameters=new HashMap<Integer, DragonPrepareStatement.ParamSetting>();
        }
        parameters.put(parameters.size()+1,paramSetting);
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

    public void setTargetStatement(PreparedStatement targetStatement) {
        this.targetStatement = targetStatement;
    }

    public PreparedStatement getTargetStatement() {
        return targetStatement;
    }

    public String getRealDBName() {
        return realDBName;
    }

    public String getPrimaryTBName() {
        return primaryTBName;
    }

    public Long getPrimaryTBIndex() {
        return primaryTBIndex;
    }
}
