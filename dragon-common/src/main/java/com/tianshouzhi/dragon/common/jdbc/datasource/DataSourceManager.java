package com.tianshouzhi.dragon.common.jdbc.datasource;

//import com.sun.xml.internal.ws.util.StringUtils;

import com.tianshouzhi.dragon.common.exception.DragonException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public abstract class DataSourceManager {
    private static final Logger LOGGER= LoggerFactory.getLogger(DataSourceManager.class);
    protected ConcurrentMap<String,DataSource> indexDSMap =new ConcurrentHashMap<String, DataSource>();
    protected void add(String datasourceIndex,DataSource dataSource){
        if(datasourceIndex ==null||dataSource==null){
            throw new IllegalArgumentException("paramter datasourceIndex and dataSource can't be null");
        }
       indexDSMap.put(datasourceIndex, dataSource);
    }
    protected void remove(String datasourceIndex){
        if(StringUtils.isBlank(datasourceIndex)){
           return;
        }
        DataSource remove = indexDSMap.remove(datasourceIndex);
        if(remove==null){
            LOGGER.warn("no datasource find by index:{},doesn't remove any datasource",datasourceIndex);
        }
    }

    protected DataSource getDatasourceByTndex(String index) throws DragonException {
        if(StringUtils.isBlank(index)){
            throw new IllegalArgumentException("paramter 'index' can't be null");
        }
        DataSource dataSource = indexDSMap.get(index);
        if(dataSource==null){
            throw new DragonException("can't find dataSource with index: "+index);
        }
        return dataSource;
    }

    protected Map<String,DataSource> getEntryByIndexes(String...indexes) throws DragonException {
        if(StringUtils.isAnyBlank(indexes)||indexes.length==0){
            throw new IllegalArgumentException("paramter 'indexes' can't be null");
        }
        Map<String,DataSource> map=new HashMap<String, DataSource>();
        for (String index : indexes) {
            map.put(index,getDatasourceByTndex(index));
        }
        return map;
    }

    protected Set<DataSource> getDatasourceByTndexes(String...indexes) throws DragonException {
        if(StringUtils.isAnyBlank(indexes)||indexes.length==0){
            throw new IllegalArgumentException("paramter 'indexes' can't be null");
        }
        HashSet<DataSource> dataSources = new HashSet<DataSource>();
        for (String index : indexes) {
            dataSources.add(getDatasourceByTndex(index));
        }
        return dataSources;
    }

    /**
     * @param indexes
     * @return one of dataource which index is member of indexes array,if not found return null
     * @throws DragonException
     */
    protected DataSource getDatasourceExcludeTndexes(String...indexes) throws DragonException {
        DataSource dataSource=null;
        Iterator<Map.Entry<String, DataSource>> iterator = indexDSMap.entrySet().iterator();
        while (dataSource!=null&&iterator.hasNext()){
            Map.Entry<String, DataSource> next = iterator.next();
            String index = next.getKey();
            for (String currentIndex : indexes) {
                if(index.equals(currentIndex)){
                    dataSource=next.getValue();
                    break;
                }
            }
        }
        return dataSource;
    }
}
