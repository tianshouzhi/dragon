package com.tianshouzhi.dragon.sharding.route;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 每个逻辑表 管理了 物理表 ，每个物理表 对应一个读写分离数据源编号
 */
public class LogicTable extends LogicConfig{
    private LogicDatabase logicDatabase;
    private Set<String> dbTbShardColumns;
    private String logicName;
    public LogicTable(String logicName,String namePattern, List<String> routeRuleStrList,LogicDatabase logicDatabase) {
        super(namePattern, routeRuleStrList);
        if(logicDatabase==null){
            throw new NullPointerException();
        }
        this.logicName=logicName;
        this.logicDatabase=logicDatabase;
        dbTbShardColumns=new HashSet<String>();
        dbTbShardColumns.addAll(logicDatabase.getMergedShardColumns()) ;
        dbTbShardColumns.addAll(this.getMergedShardColumns());
    }
    public String getRealDBName(Map<String,Object> shardColumnValuesMap){
        return logicDatabase.getRealName(shardColumnValuesMap);
    }
    public Long getRealDBIndex(Map<String,Object> shardColumnValuesMap){
        return logicDatabase.getRealIndex(shardColumnValuesMap);
    }
    public String getRealTBName(Map<String,Object> shardColumnValuesMap){
        return getRealName(shardColumnValuesMap);
    }
    public Long getRealTBIndex(Map<String,Object> shardColumnValuesMap){
        return getRealIndex(shardColumnValuesMap);
    }
    public String getLogicName() {
        return logicName;
    }
    /**
     * 将dbrule中指定的所有分区字段、tbrule中指定的所有分区字段合并在一起
     * @return
     */
    public Set<String> getDbTbShardColumns() {
        return dbTbShardColumns;
    }
}
