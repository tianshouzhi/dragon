package com.tianshouzhi.dragon.common.initailzer;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Created by TIANSHOUZHI336 on 2017/3/20.
 */
public abstract class DataSourceInitailzerAdapter {
    private static ServiceLoader<DataSourceInitailzer> serviceLoader=ServiceLoader.load(DataSourceInitailzer.class);;
    private static Map<String,DataSourceInitailzer> classNameInitailzerMap=new HashMap<String, DataSourceInitailzer>();
    static {
        Iterator<DataSourceInitailzer> iterator = serviceLoader.iterator();
        classNameInitailzerMap=new HashMap<String, DataSourceInitailzer>();
        while (iterator.hasNext()){
            DataSourceInitailzer dataSourceInitailzer = iterator.next();
            String className = dataSourceInitailzer.initDatasouceClassName();
            classNameInitailzerMap.put(className,dataSourceInitailzer);
        }
    }
    public DataSource init(String datasourceClass,Map<String,String> config){
        DataSourceInitailzer dataSourceInitailzer = classNameInitailzerMap.get(datasourceClass);
        if(dataSourceInitailzer==null){
            throw new RuntimeException("can't init datasource type:"+datasourceClass+",you should custom a DataSourceInitailzer and add in the classpath");
        }
        return dataSourceInitailzer.init(config);
    }
}
