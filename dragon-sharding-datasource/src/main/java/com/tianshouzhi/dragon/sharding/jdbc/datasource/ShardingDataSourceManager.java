package com.tianshouzhi.dragon.sharding.jdbc.datasource;

import com.tianshouzhi.dragon.common.jdbc.datasource.DataSourceIndex;
import com.tianshouzhi.dragon.ha.jdbc.datasource.DragonHADatasource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by TIANSHOUZHI336 on 2016/12/16.
 */
public class ShardingDataSourceManager {
    private Map<DataSourceIndex,DragonHADatasource> map=new ConcurrentHashMap<DataSourceIndex, DragonHADatasource>();
    public DragonHADatasource getDragonHADatasource(String haIndex){
        return map.get(haIndex);
    }
    public Map<String,DragonHADatasource> getDragonHADatasource(String...haIndexes){
        Map<String,DragonHADatasource> result=new HashMap<String, DragonHADatasource>();
        for (String haIndex : haIndexes) {
            result.put(haIndex,map.get(haIndex));
        }
        return result;
    }
}
