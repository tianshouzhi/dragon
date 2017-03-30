package com.tianshouzhi.dragon.sharding.pipeline.handler.statics;

import java.util.List;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2017/3/22.
 */
public class SqlExecutionStatics {

    private String originSql;
    private String originParamters;
    private boolean query;
    private boolean prepare;
    private boolean success;
    private String exception;
    private int totalUpdateCount;
    private int returnRowCount;
    private int queryRowCount;
    private long routeSqlNums;
    private long beginTime;
    private long totalExecuteTime;
    private boolean hitSqlParserCache;
    private long sqlParseTimeMillis;
    private long sqlRewriteTimeMillis;
    private int parallelExecutionTaskNum;
    private long parallelExecutionTimeMillis;
    private long resultMergeTimeMillis;
    private Map<String, List<SqlRouteDetail>> routeDetailMap;
    private String ip;
    private String appName;

    public void setOriginSql(String originSql) {
        this.originSql = originSql;
    }

    public String getOriginSql() {
        return originSql;
    }

    public void setOriginParamters(String originParamters) {
        this.originParamters = originParamters;
    }

    public String getOriginParamters() {
        return originParamters;
    }

    public void setQuery(boolean query) {
        this.query = query;
    }

    public boolean isQuery() {
        return query;
    }

    public void setPrepare(boolean prepare) {
        this.prepare = prepare;
    }

    public boolean isPrepare() {
        return prepare;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getException() {
        return exception;
    }

    public void setTotalUpdateCount(int totalUpdateCount) {
        this.totalUpdateCount = totalUpdateCount;
    }

    public int getTotalUpdateCount() {
        return totalUpdateCount;
    }

    public void setReturnRowCount(int returnRowCount) {
        this.returnRowCount = returnRowCount;
    }

    public int getReturnRowCount() {
        return returnRowCount;
    }

    public void setQueryRowCount(int queryRowCount) {
        this.queryRowCount = queryRowCount;
    }

    public int getQueryRowCount() {
        return queryRowCount;
    }

    public void setRouteSqlNums(long routeSqlNums) {
        this.routeSqlNums = routeSqlNums;
    }

    public long getRouteSqlNums() {
        return routeSqlNums;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setTotalExecuteTime(long totalExecuteTime) {
        this.totalExecuteTime = totalExecuteTime;
    }

    public long getTotalExecuteTime() {
        return totalExecuteTime;
    }

    public void setHitSqlParserCache(boolean hitSqlParserCache) {
        this.hitSqlParserCache = hitSqlParserCache;
    }

    public boolean isHitSqlParserCache() {
        return hitSqlParserCache;
    }

    public void setSqlParseTimeMillis(long sqlParseTimeMillis) {
        this.sqlParseTimeMillis = sqlParseTimeMillis;
    }

    public long getSqlParseTimeMillis() {
        return sqlParseTimeMillis;
    }

    public void setSqlRewriteTimeMillis(long sqlRewriteTimeMillis) {
        this.sqlRewriteTimeMillis = sqlRewriteTimeMillis;
    }

    public long getSqlRewriteTimeMillis() {
        return sqlRewriteTimeMillis;
    }

    public void setParallelExecutionTaskNum(int parallelExecutionTaskNum) {
        this.parallelExecutionTaskNum = parallelExecutionTaskNum;
    }

    public int getParallelExecutionTaskNum() {
        return parallelExecutionTaskNum;
    }

    public void setParallelExecutionTimeMillis(long parallelExecutionTimeMillis) {
        this.parallelExecutionTimeMillis = parallelExecutionTimeMillis;
    }

    public long getParallelExecutionTimeMillis() {
        return parallelExecutionTimeMillis;
    }

    public void setResultMergeTimeMillis(long resultMergeTimeMillis) {
        this.resultMergeTimeMillis = resultMergeTimeMillis;
    }

    public long getResultMergeTimeMillis() {
        return resultMergeTimeMillis;
    }

    public Map<String, List<SqlRouteDetail>> getRouteDetailMap() {
        return routeDetailMap;
    }

    public void setRouteDetailMap(Map<String, List<SqlRouteDetail>> routeDetailMap) {
        this.routeDetailMap = routeDetailMap;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }


    public String getAppName() {
        return appName;
    }
}
