package com.tianshouzhi.dragon.sharding.route;

import java.util.*;

/**
 * 每个逻辑表 管理了 物理表 ，每个物理表 对应一个读写分离数据源编号
 */
public class LogicTable extends LogicConfig{
    private LogicDatabase logicDatabase;
    private Set<String> dbTbShardColumns;
    private String logicTableName;
    /**真实库和表的对应关系，可以不设置，但是如果不设置的话，无法从所有分库进行查询*/
    private Map<String,List<String>> realDBTBMap;
    public LogicTable(String logicTableName, String namePattern, List<String> routeRuleStrList, LogicDatabase logicDatabase) {
       this(logicTableName,namePattern,routeRuleStrList,logicDatabase,null);
    }

    /**
     * @param logicTableName
     * @param tableNameFormat
     * @param routeRuleStrList
     * @param logicDatabase
     * @param realDBTBMap
     */
    public LogicTable(String logicTableName,String tableNameFormat, List<String> routeRuleStrList,LogicDatabase logicDatabase,Map<String,List<String>> realDBTBMap) {
        super(tableNameFormat, routeRuleStrList);
        if(logicDatabase==null){
            throw new NullPointerException();
        }
        this.logicTableName =logicTableName;
        this.logicDatabase=logicDatabase;
        dbTbShardColumns=new HashSet<String>();
        dbTbShardColumns.addAll(logicDatabase.getMergedShardColumns()) ;
        dbTbShardColumns.addAll(this.getMergedShardColumns());
        this.realDBTBMap = realDBTBMap;
    }

    /**
     * 根据路由参数，获取真实库名
     * @param shardColumnValuesMap
     * @return
     */
    public String getRealDBName(Map<String,Object> shardColumnValuesMap){
        return logicDatabase.getRealName(shardColumnValuesMap);
    }
    public Long getRealDBIndex(Map<String,Object> shardColumnValuesMap){
        return logicDatabase.getRealIndex(shardColumnValuesMap);
    }
    public String getRealTBName(Map<String,Object> shardColumnValuesMap){
        return getRealName(shardColumnValuesMap);
    }

    /**
     * 根据路由参数，计算真实索引
     * @param shardColumnValuesMap
     * @return
     */
    public Long getRealTBIndex(Map<String,Object> shardColumnValuesMap){
        return getRealIndex(shardColumnValuesMap);
    }

    /**
     * 根据真实库名解析Index 例如 user_1010，解析后的值为1010
     * @param realTBName
     * @return
     */
    public Long parseRealTBIndex(String realTBName) {
        return super.parseIndex(realTBName);
    }
    public Long parseRealDBIndex(String realDBName) {
        return logicDatabase.parseIndex(realDBName);
    }
    public String getLogicTableName() {
        return logicTableName;
    }
    /**
     * 将dbrule中指定的所有分区字段、tbrule中指定的所有分区字段合并在一起
     * @return
     */
    public Set<String> getDbTbShardColumns() {
        return dbTbShardColumns;
    }

    public Map<String, List<String>> getRealDBTBMap() {
        return realDBTBMap;
    }

    public void setRealDBTBMap(Map<String, List<String>> realDBTBMap) {
        if(this.realDBTBMap==null){
            this.realDBTBMap=new TreeMap<String, List<String>>();
        }
        this.realDBTBMap.putAll(realDBTBMap);
    }
}
