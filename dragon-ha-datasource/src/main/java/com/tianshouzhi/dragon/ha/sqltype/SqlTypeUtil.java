package com.tianshouzhi.dragon.ha.sqltype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class SqlTypeUtil {
    private static final Logger LOGGER= LoggerFactory.getLogger(SqlTypeUtil.class);
    private static Map<String,Boolean> sqlTypeCache=new ConcurrentHashMap<String, Boolean>();

    /**
     * 传入的应该是一条sql，不应该将多条sql放在一起传入，且SQL前面不能包含任何形式的注释，包括hint
     * @param sql
     * @return
     * @throws SQLException
     */
    public static boolean isQuery(String sql,boolean useCache) throws SQLException {
        Boolean isQuery=null;
        if(useCache){
            isQuery = sqlTypeCache.get(sql);
        }
        if(isQuery!=null){//命中了cache
            LOGGER.debug("hit cache,sql:{} is {}",sql,isQuery);
            return isQuery;
        }

        //如果不需要使用cache或者没有命中cache
        SqlType sqlType = parseSqlType(sql);
        switch (sqlType) {
//            case SELECT_FOR_UPDATE:
            case SELECT:
            case SHOW:
            case DEBUG:
            case EXPLAIN:
            case DUMP:
                isQuery= true;
                break;
            case INSERT:
            case UPDATE:
            case DELETE:
            case REPLACE:
            case TRUNCATE:
            case CREATE:
            case DROP:
            case LOAD:
            case MERGE:
            case ALTER:
            case RENAME:
            case CALL:
                isQuery= false;
                break;
        }
        if(useCache){
            sqlTypeCache.put(sql,isQuery);
        }
        return isQuery;
    }

    public static SqlType parseSqlType(String sql) throws SQLException {
        //parse sql
        SqlType[] values = SqlType.values();
        for (SqlType current : values) {
            Pattern pattern = current.getPattern();
            Matcher matcher = pattern.matcher(sql);
            if(matcher.matches()){
                return current;
            }
        }
        throw new UnsupportedOperationException("only select, insert, update, delete, replace, show, truncate, create, drop, load, merge, dump sql is supported");
    }
}
