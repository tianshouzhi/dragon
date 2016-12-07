package com.tianshouzhi.dragon.ha.sqltype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class SqlTypeUtil {
    private static final Logger LOGGER= LoggerFactory.getLogger(SqlTypeUtil.class);
    private static SqlTypeCache sqlTypeCache=new SqlTypeCache();

    /**
     * 传入的应该是一条sql，不应该将多条sql放在一起传入，且SQL前面不能包含任何形式的注释，包括hint
     * @param sql
     * @return
     * @throws SQLException
     */
    public static boolean isQuery(String sql) throws SQLException {
        /*Boolean isQuery = sqlTypeCache.get(sql);
        if(isQuery!=null){
            LOGGER.debug("hit cache,sql:{} is {}",sql,isQuery);
            return isQuery;
        }*/
        SqlType sqlType = parseSqlType(sql);
        switch (sqlType) {
//            case SELECT_FOR_UPDATE:
            case SELECT:
            case SHOW:
            case DEBUG:
            case EXPLAIN:
            case DUMP:
                return true;
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
                return false;
            default:
                throw new SQLException("only select, insert, update, delete, replace, show, truncate, create, drop, load, merge, dump sql is supported");

        }
    }

    public static SqlType parseSqlType(String sql) {
        //parse sql
        SqlType[] values = SqlType.values();
        for (SqlType current : values) {
            Pattern pattern = current.getPattern();
            Matcher matcher = pattern.matcher(sql);
            if(matcher.matches()){
                return current;
            }
        }
        return null;
    }
}
