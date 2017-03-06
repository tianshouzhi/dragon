package com.tianshouzhi.dragon.sharding.route;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2016/12/15.
 */
public class Router {
    private LogicDatabase logicDatabase;
    /**
     * key为逻辑表名，value是对应的逻辑表对象
     */
    private Map<String,LogicTable> logicTableMap;

    public Router(LogicDatabase logicDatabase, Map<String, LogicTable> logicTableMap) {
        if(logicDatabase==null||logicTableMap==null){
            throw new NullPointerException();
        }
        this.logicDatabase = logicDatabase;
        this.logicTableMap = logicTableMap;
    }

    public LogicDatabase getLogicDatabase() {
        return logicDatabase;
    }

    public Map<String, LogicTable> getLogicTableMap() {
        return logicTableMap;
    }

    public LogicTable getLogicTable(String logicTableName) {
        return logicTableMap.get(logicTableName);
    }

    public DataSource getDataSource(String dbIndex) {
        return logicDatabase.getDatasource(dbIndex);
    }
}
