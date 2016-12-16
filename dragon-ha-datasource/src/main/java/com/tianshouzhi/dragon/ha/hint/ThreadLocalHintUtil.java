package com.tianshouzhi.dragon.ha.hint;

import com.tianshouzhi.dragon.common.jdbc.datasource.DataSourceIndex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TIANSHOUZHI336 on 2016/12/4.
 */
public class ThreadLocalHintUtil {
    private static final String DB_INDEX_KEY="DB_INDEX_HINT";
    private static final ThreadLocal< List<DataSourceIndex>> hint=new ThreadLocal< List<DataSourceIndex>>();
    public static void setDBIndexes(String ... dbIndexes){
        if(dbIndexes!=null&&dbIndexes.length>0){
            List<DataSourceIndex> dataSourceIndexList =new ArrayList<DataSourceIndex>();
            for (String dbIndex : dbIndexes) {
                dataSourceIndexList.add(new DataSourceIndex(dbIndex));
            }
            hint.set(dataSourceIndexList);
        }
    }
    public static List<DataSourceIndex> getHintDataSourceIndexes(){
        return hint.get();
    }

    public static void remove(){
        hint.remove();
    }
}
