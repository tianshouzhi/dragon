package com.tianshouzhi.dragon.ha.hint;

import com.tianshouzhi.dragon.ha.dbselector.DBIndex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TIANSHOUZHI336 on 2016/12/4.
 */
public class ThreadLocalHintUtil {
    private static final String DB_INDEX_KEY="DB_INDEX_HINT";
    private static final ThreadLocal< List<DBIndex>> hint=new ThreadLocal< List<DBIndex>>();
    public static void setDBIndexes(String ... dbIndexes){
        if(dbIndexes!=null&&dbIndexes.length>0){
            List<DBIndex> dbIndexList=new ArrayList<DBIndex>();
            for (String dbIndex : dbIndexes) {
                dbIndexList.add(new DBIndex(dbIndex));
            }
            hint.set(dbIndexList);
        }
    }
    public static List<DBIndex> getHintDBIndexes(){
        return hint.get();
    }

    public static void remove(){
        hint.remove();
    }
}
