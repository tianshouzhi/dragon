package com.tianshouzhi.dragon.ha.sqltype;

import org.junit.Test;

import java.sql.SQLException;

/**
 * Created by TIANSHOUZHI336 on 2016/12/7.
 */
public class SqlTypeUtilTest {
    @Test
    public void test() throws SQLException {
        boolean query = SqlTypeUtil.isQuery("select * from");

    }
}
