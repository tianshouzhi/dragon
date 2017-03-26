package com.tianshouzhi.dragon.sharding.route;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by TIANSHOUZHI336 on 2017/2/19.
 */
public class LogicDatasouce extends LogicConfig{
    private String defaultDSName; //主要用于处理只分表，不分库的情况，可能只有部分表的数据比较多，需要进行分表
    /**
     * 数据库名称与对应的数据源的映射关系,TreeMap会根据key排序
     */
    private Map<String,DataSource> dbIndexDatasourceMap=new TreeMap<String, DataSource>();
    public LogicDatasouce(String dbNameFormat, Map<String,DataSource> dbIndexDatasourceMap) {
        super(dbNameFormat);
        if(MapUtils.isEmpty(dbIndexDatasourceMap)){
            throw new IllegalArgumentException("dbIndexDatasourceMap can't be null or empty!!!");
        }
        this.dbIndexDatasourceMap.putAll(dbIndexDatasourceMap);
        //如果defaultRealDSName！=null，但是dbIndexDatasourceMap不包含defaultRealDSName，说明配置错误
        if(StringUtils.isNoneEmpty(defaultDSName)&&!dbIndexDatasourceMap.containsKey(defaultDSName)){
            throw new IllegalArgumentException("dbIndexDatasourceMap doesn't contains defaultDSName:"+ defaultDSName);
        }
        //todo 检查dbIndex(即map的key)和namePattern是否匹配
    }

    public DataSource getDatasource(String dbIndex){
        return dbIndexDatasourceMap.get(dbIndex);
    }

    public Map<String, DataSource> getRealDbIndexDatasourceMap() {
        return dbIndexDatasourceMap;
    }

    public String getDefaultDSName() {
        return defaultDSName;
    }

    public void setDefaultDSName(String defaultDSName) {
        this.defaultDSName = defaultDSName;
    }
}
