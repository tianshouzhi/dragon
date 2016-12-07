package com.tianshouzhi.dragon.ha.sqltype;

import java.util.regex.Pattern;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public enum SqlType {
//    SELECT_FOR_UPDATE("\\s*SELECT.+"),
    SELECT("\\s*SELECT.+"),
    SHOW("\\s*SHOW.+"),
    DEBUG("\\s*DEBUG.+"),
    EXPLAIN("\\S+EXPLAIN.+"),
    DUMP("\\s*DUMP.+"),

    INSERT("\\s*INSERT.+"),
    UPDATE("\\s*EXECUTE_UPDATE.+"),
    DELETE("\\s*DELETE.+"),
    REPLACE("\\s*REPLACE.+"),
    TRUNCATE("\\s*TRUNCATE.+"),
    CREATE("\\s*TRUNCATE.+"),
    DROP("\\s*TRUNCATE.+"),
    LOAD("\\s*TRUNCATE.+"),
    MERGE("\\s*TRUNCATE.+"),
    ALTER("\\s*TRUNCATE.+"),
    RENAME("\\s*TRUNCATE.+"),
    CALL("\\s*CALL.+");//存储过程

    Pattern pattern;

    SqlType(String parttern) {
        pattern=Pattern.compile(parttern,Pattern.CASE_INSENSITIVE/*|Pattern.MULTILINE*/);
    }

    public Pattern getPattern() {
        return pattern;
    }
}
