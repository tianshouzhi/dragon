package com.tianshouzhi.dragon.sharding.pipeline.handler.statics;

/**
 * Created by TIANSHOUZHI336 on 2017/3/29.
 */
public class SqlRouteDetail {
    private String sql;
    private long executionTimeMillis;
    private String params;

    public SqlRouteDetail(String sql, long executionTimeMillis, String params) {
        this.sql = sql;
        this.executionTimeMillis = executionTimeMillis;
        this.params = params;
    }

    public long getExecutionTimeMillis() {
        return executionTimeMillis;
    }

    public String getSql() {
        return sql;
    }

    public String getParams() {
        return params;
    }
}
