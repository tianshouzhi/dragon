package com.tianshouzhi.dragon.ha.hint;

import com.tianshouzhi.dragon.ha.dbselector.DBIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by TIANSHOUZHI336 on 2016/12/4.
 */
public class SqlHintUtil {

    //匹配hint前缀:/*DRAGON_DYNAMIC(
    private static final String SQL_HINT_PREFIX_GROUP="(/\\*\\s*DRAGON_HA\\s*\\()";
    //匹配hint中的DBIDEX标识
    private static final String SQL_HINT_DBIDEX_GROUP="(\\s*DBINDEXES\\s*=)\\s*";
    //匹配出的DBIndex
    private static final String SQL_DBIDEX_GROUP="(\\w+[,\\w+]*)";
    //匹配hint后缀:)*/
    private static final String SQL_HINT_POSTFIX_GROUP="(\\s*\\)\\s*\\*/)";
    //匹配sql与hint之间的部分
    private static final String SQL_GROUP="(.+)";
    private static Pattern hintParttern=Pattern.compile(
            SQL_HINT_PREFIX_GROUP+SQL_HINT_DBIDEX_GROUP +SQL_DBIDEX_GROUP+SQL_HINT_POSTFIX_GROUP+SQL_GROUP,
            Pattern.CASE_INSENSITIVE);

    public static List<DBIndex> getSQLHintDBIndex(String sql){
        if(sql==null){
            return null;
        }

        Matcher matcher = hintParttern.matcher(sql);
        if(!matcher.matches()){
            return null;
        }
        String dbIndex = matcher.group(3);//第0个group是整体，因此第3个
        String[] split = dbIndex.split(",");
        List<DBIndex> dbIndexList=new ArrayList<DBIndex>();{
            for (String s : split) {
                dbIndexList.add(new DBIndex(s));
            }
        }
        return dbIndexList;
    }

    public static void main(String[] args) {
        Matcher matcher = hintParttern.matcher("/*DRAGON_HA ( DBINDEXES = yourDbIndex[,...] )*/ SELECT * FROM user");
        if(matcher.matches()){
            int i = matcher.groupCount();
            for (int j = 0; j <i; j++) {
                System.out.println(matcher.group(j));
            }
        }
    }
}
