package com.tianshouzhi.dragon.sqlparser.parser;

import com.tianshouzhi.dragon.sqlparser.ast.SQLStatement;

/**
 * Created by TIANSHOUZHI336 on 2016/12/15.
 */
public interface SQLStatementParser {
    public SQLStatement parse(String sql);
}
