package com.tianshouzhi.dragon.common.util;

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
    UPDATE("\\s*UPDATE.+"),
    DELETE("\\s*DELETE.+"),
    REPLACE("\\s*REPLACE.+"),
    TRUNCATE("\\s*TRUNCATE.+"),
    CREATE("\\s*CREATE.+"),
    DROP("\\s*DROP.+"),
    LOAD("\\s*LOAD.+"),
    MERGE("\\s*MERGE.+"),
    ALTER("\\s*ALTER.+"),
    RENAME("\\s*RENAME.+"),
    CALL("\\s*CALL.+");//存储过程

    Pattern pattern;

    SqlType(String parttern) {
        pattern=Pattern.compile(parttern,Pattern.CASE_INSENSITIVE/*|Pattern.MULTILINE*/);
    }

    public Pattern getPattern() {
        return pattern;
    }
}
