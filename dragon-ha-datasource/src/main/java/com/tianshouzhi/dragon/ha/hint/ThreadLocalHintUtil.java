package com.tianshouzhi.dragon.ha.hint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TIANSHOUZHI336 on 2016/12/4.
 */
public class ThreadLocalHintUtil {
    private static final ThreadLocal< List<String>> hint=new ThreadLocal< List<String>>();
    public static void setDBIndexes(String ... dbIndexes){
        if(dbIndexes!=null&&dbIndexes.length>0){
            List<String> dataSourceIndexList =new ArrayList<String>();
            for (String dbIndex : dbIndexes) {
                dataSourceIndexList.add(dbIndex);
            }
            hint.set(dataSourceIndexList);
        }
    }
    public static List<String> getHintDataSourceIndexes(){
        return hint.get();
    }

    public static void remove(){
        hint.remove();
    }
}
