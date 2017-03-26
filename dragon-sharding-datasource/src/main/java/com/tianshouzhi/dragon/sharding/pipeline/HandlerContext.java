package com.tianshouzhi.dragon.sharding.pipeline;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.tianshouzhi.dragon.sharding.jdbc.datasource.DragonShardingConfig;
import com.tianshouzhi.dragon.sharding.jdbc.datasource.DragonShardingDataSource;
import com.tianshouzhi.dragon.sharding.jdbc.resultset.DragonShardingResultSet;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingStatement;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRouteInfo;
import com.tianshouzhi.dragon.sharding.route.LogicDatasouce;
import com.tianshouzhi.dragon.sharding.route.LogicTable;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created by TIANSHOUZHI336 on 2017/2/24.
 */
public class HandlerContext {
    private DragonShardingStatement dragonShardingStatement;
    private SQLStatement parsedSqlStatement;
    private Map<String/**dbIndex*/,Set<String/**tbIndex*/>> hintMap=new HashMap<String, Set<String>>();
    private Map<String/**dbIndex*/, Map<String/*tbIndex*/, SqlRouteInfo>> sqlRouteMap;

    //存储执行结果
    private int totalUpdateCount =-1;
    private DragonShardingResultSet mergedResultSet;
    private boolean isQuery;

    //limit 信息 todo 有待完善 mysql rowCount=-1 表示从当前读取到最后
    private int offset=-1;
    private int rowCount=-1;
    private Map<String, String> fullColumnNameAliasMap;

    //sql执行的开始时间 ，用于统计
    private long beginTime=System.currentTimeMillis();
    private int originQueryCount;
    private Throwable throwable;
    private boolean hitSqlParserCache;
    private long sqlParseTimeMillis;
    private long resultMergeTime;
    private long sqlRewriteTimeMillis;
    private long parallelExecutionTimeMillis;
    private int parallelExecutionTaskNum;

    public HandlerContext(DragonShardingStatement dragonShardingStatement) {
        if(dragonShardingStatement==null){
            throw new NullPointerException();
        }
        this.dragonShardingStatement = dragonShardingStatement;
        this.sqlRouteMap=new TreeMap<String, Map<String, SqlRouteInfo>>();
    }
    public DataSource getRealDataSource(String realDBName){
        return getDragonShardingConfig().getLogicDatasouce().getDatasource(realDBName);
    }
    public DragonShardingStatement getDragonShardingStatement() {
        return dragonShardingStatement;
    }

    public SQLStatement getParsedSqlStatement() {
        return parsedSqlStatement;
    }

    public void setParsedSqlStatement(SQLStatement parsedSqlStatement) {
        this.parsedSqlStatement = parsedSqlStatement;
    }

    public Map<String, Map<String, SqlRouteInfo>> getSqlRouteMap() {
        return sqlRouteMap;
    }

    public int getTotalUpdateCount() {
        return totalUpdateCount;
    }

    public void setTotalUpdateCount(int totalUpdateCount) {
        this.totalUpdateCount = totalUpdateCount;
    }

    public DragonShardingResultSet getMergedResultSet() {
        return mergedResultSet;
    }

    public void setMergedResultSet(DragonShardingResultSet mergedResultSet) {
        this.mergedResultSet = mergedResultSet;
    }

    public void setIsQuery(boolean isQuery) {
        this.isQuery = isQuery;
    }

    public boolean isQuery() {
        return isQuery;
    }

    public Map<String, Set<String>> getHintMap() {
        return hintMap;
    }

    public void setHintMap(Map<String, Set<String>> hintMap) {
        this.hintMap = hintMap;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * todo 优化，不需要每一次都计算
     * @return
     */
    public List<Statement> getRealStatementList() {
        Iterator<Map.Entry<String, Map<String, SqlRouteInfo>>> dbIterator = sqlRouteMap.entrySet().iterator();
        List<Statement> statementList = new ArrayList<Statement>();
        while (dbIterator.hasNext()) {
            Map.Entry<String, Map<String, SqlRouteInfo>> entry = dbIterator.next();
            String dbIndex = entry.getKey();
            Map<String, SqlRouteInfo> tbSqlMap = entry.getValue();
            Iterator<Map.Entry<String, SqlRouteInfo>> tbIterator = tbSqlMap.entrySet().iterator();
            while (tbIterator.hasNext()) {
                Map.Entry<String, SqlRouteInfo> tableResult = tbIterator.next();
                PreparedStatement targetStatement = tableResult.getValue().getTargetStatement();
                statementList.add(targetStatement);
            }
        }
        return statementList;
    }

    public void setFullColumnNameAliasMap(Map<String, String> fullColumnNameAliasMap) {
        this.fullColumnNameAliasMap = fullColumnNameAliasMap;
    }

    public Map<String, String> getFullColumnNameAliasMap() {
        return fullColumnNameAliasMap;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setOriginQueryCount(int originQueryCount) {
        this.originQueryCount = originQueryCount;
    }

    public int getOriginQueryCount() {
        return originQueryCount;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setHitSqlParserCache(boolean hitSqlParserCache) {
        this.hitSqlParserCache = hitSqlParserCache;
    }

    public boolean isHitSqlParserCache() {
        return hitSqlParserCache;
    }

    public long getSqlParseTimeMillis() {
        return sqlParseTimeMillis;
    }

    public void setSqlParseTimeMillis(long sqlParseTimeMillis) {
        this.sqlParseTimeMillis = sqlParseTimeMillis;
    }

    public void setResultMergeTime(long resultMergeTime) {
        this.resultMergeTime = resultMergeTime;
    }

    public long getResultMergeTimeMillis() {
        return resultMergeTime;
    }

    public void setSqlRewriteTimeMillis(long sqlRewriteTimeMillis) {
        this.sqlRewriteTimeMillis = sqlRewriteTimeMillis;
    }

    public long getSqlRewriteTimeMillis() {
        return sqlRewriteTimeMillis;
    }

    public void setParallelExecutionTimeMillis(long parallelExecutionTimeMillis) {
        this.parallelExecutionTimeMillis = parallelExecutionTimeMillis;
    }

    public long getParallelExecutionTimeMillis() {
        return parallelExecutionTimeMillis;
    }

    public int getParallelExecutionTaskNum() {
        return parallelExecutionTaskNum;
    }

    public void setParallelExecutionTaskNum(int tarallelExecutionTaskNum) {
        this.parallelExecutionTaskNum = tarallelExecutionTaskNum;
    }

    public LogicDatasouce getLogicDataSource() {
        return getDragonShardingConfig().getLogicDatasouce();
    }

    public DragonShardingConfig getDragonShardingConfig(){
        try {
            return dragonShardingStatement.getConnection().getDragonShardingConfig();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public LogicTable getLogicTable(String logicTableName) {
        return getDragonShardingConfig().getLogicTableMap().get(logicTableName);
    }
}
