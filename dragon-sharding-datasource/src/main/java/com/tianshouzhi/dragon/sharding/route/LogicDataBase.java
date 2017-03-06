package com.tianshouzhi.dragon.sharding.route;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2017/2/19.
 */
public class LogicDatabase extends LogicConfig{
    /**
     * 数据库名称与对应的数据源的映射关系
     */
    private Map<String,DataSource> dbIndexDatasourceMap;
    public LogicDatabase(String dbNameFormat, List<String> routeRuleStrList,Map<String,DataSource> dbIndexDatasourceMap) {
        super(dbNameFormat, routeRuleStrList);
        if(dbIndexDatasourceMap==null||dbIndexDatasourceMap.size()==0){
            throw new RuntimeException("dbIndexDatasourceMap can't be null or empty!!!");
        }
        this.dbIndexDatasourceMap=dbIndexDatasourceMap;
        //todo 检查dbIndex(即map的key)和namePattern是否匹配

    }

    public DataSource getDatasource(String dbIndex){
        return dbIndexDatasourceMap.get(dbIndex);
    }
}
