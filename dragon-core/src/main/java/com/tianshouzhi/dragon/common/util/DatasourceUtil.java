package com.tianshouzhi.dragon.common.util;

import com.tianshouzhi.dragon.common.log.Log;
import com.tianshouzhi.dragon.common.log.LoggerFactory;
import com.tianshouzhi.dragon.common.thread.DragonThreadFactory;
import com.tianshouzhi.dragon.ha.util.DatasourceSpiUtil;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by tianshouzhi on 2017/11/1.
 */
public class DatasourceUtil {

    private static final Log LOG= LoggerFactory.getLogger(DatasourceSpiUtil.class);

    public static Set<String> datasourceNames =new ConcurrentSkipListSet<String>();
    public static ExecutorService executor=Executors.newSingleThreadExecutor(new DragonThreadFactory("DRAONG_ASYNC_CLOSE_THREAD",true));

    public static synchronized String generateDataSourceName(String name){
        if(!datasourceNames.contains(name)){
            datasourceNames.add(name);
            return name;
        }else{
            for (int i=1;;i++){
                name=name+i;
                if(!datasourceNames.contains(name)){
                    datasourceNames.add(name);
                    return name;
                }
            }
        }
    }
    public static DataSource createDataSource(Class<? extends DataSource> clazz, Properties properties) throws Exception {
        DataSource dataSource = clazz.newInstance();
        BeanPropertyUtil.populate(dataSource,properties);
        return dataSource;
    }

    public static void init(DataSource dataSource){
        try {
            Method closeMethod = dataSource.getClass().getDeclaredMethod("init");
            if(closeMethod!=null){
                closeMethod.invoke(dataSource);
            }
        } catch (Exception ignore) {
        }
    }

    public static void close(final String haDSName, final String realDSName,DataSource dataSource){
        try {
            Method closeMethod = dataSource.getClass().getDeclaredMethod("close");
            if(closeMethod!=null){
                closeMethod.invoke(dataSource);
            }
        } catch (Exception e) {
            LOG.error("close real datasource "+haDSName+"."+realDSName+" error",e);
        }
    }

    public static void close(final String haDSName, final String realDSName, final Connection connection) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOG.error("close connection from "+haDSName+"."+realDSName+" error",e);
                }
            }
        });
    }
}
