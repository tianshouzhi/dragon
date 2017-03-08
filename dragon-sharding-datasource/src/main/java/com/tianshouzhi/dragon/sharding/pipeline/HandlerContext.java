package com.tianshouzhi.dragon.sharding.pipeline;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingStatement;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRouteInfo;
import com.tianshouzhi.dragon.sharding.route.LogicTable;
import com.tianshouzhi.dragon.sharding.route.Router;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2017/2/24.
 */
public class HandlerContext {
    private DragonShardingStatement dragonShardingStatement;
    private SQLStatement parsedSqlStatement;
    private Router router;
    private Map<String, Map<String, SqlRouteInfo>> sqlRouteMap;

    //存储执行结果
    private int totalUpdateCount =-1;
    private ResultSet mergedResultSet;
    private boolean isQuery;

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
}
