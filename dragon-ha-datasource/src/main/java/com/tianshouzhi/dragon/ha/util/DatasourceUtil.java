package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.util.BeanPropertyUtil;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Created by tianshouzhi on 2017/11/1.
 */
public class DatasourceUtil {
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

    public static void close(DataSource dataSource){
        try {
            Method closeMethod = dataSource.getClass().getDeclaredMethod("close");
            if(closeMethod!=null){
                closeMethod.invoke(dataSource);
            }
        } catch (Exception ignore) {
        }
    }
}
