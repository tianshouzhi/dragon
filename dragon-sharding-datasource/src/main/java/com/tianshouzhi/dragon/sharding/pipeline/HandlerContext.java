package com.tianshouzhi.dragon.sharding.pipeline;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingStatement;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRouteInfo;
import com.tianshouzhi.dragon.sharding.route.LogicTable;
import com.tianshouzhi.dragon.sharding.route.Router;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created by TIANSHOUZHI336 on 2017/2/24.
 */
public class HandlerContext {
    private DragonShardingStatement dragonShardingStatement;
    private SQLStatement parsedSqlStatement;
    private Router router;
    private Map<String/**dbIndex*/,Set<String/**tbIndex*/>> hintMap=new HashMap<String, Set<String>>();
    private Map<String/**dbIndex*/, Map<String/*tbIndex*/, SqlRouteInfo>> sqlRouteMap;

    //存储执行结果
    private int totalUpdateCount =-1;
    private ResultSet mergedResultSet;
    private boolean isQuery;

    //limit 信息
    private int offset=-1;
    private int rowCount=-1;

    public HandlerContext(DragonShardingStatement dragonShardingStatement) {
        if(dragonShardingStatement==null){
            throw new NullPointerException();
        }
        this.dragonShardingStatement = dragonShardingStatement;
        try {
            this.router=dragonShardingStatement.getConnection().getRouter();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public DragonShardingStatement getDragonShardingStatement() {
        return dragonShardingStatement;
    }

    public LogicTable getLogicTable(String logicTableName) {
        return getRouter().getLogicTable(logicTableName);
    }

    public Router getRouter() {
        return router;
    }

    public SQLStatement getParsedSqlStatement() {
        return parsedSqlStatement;
    }

    public void setParsedSqlStatement(SQLStatement parsedSqlStatement) {
        this.parsedSqlStatement = parsedSqlStatement;
    }

    public void setSqlRouteMap(Map<String, Map<String, SqlRouteInfo>> sqlRouteMap) {
        this.sqlRouteMap = sqlRouteMap;
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

    public ResultSet getMergedResultSet() {
        return mergedResultSet;
    }

    public void setMergedResultSet(ResultSet mergedResultSet) {
        this.mergedResultSet = mergedResultSet;
    }

    public void setIsQuery(boolean isQuery) {
        this.isQuery = isQuery;
    }

    public boolean isQuery() {
        return isQuery;
    }

    public void setQuery(boolean isQuery) {
        this.isQuery = isQuery;
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
}
