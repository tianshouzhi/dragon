package com.tianshouzhi.dragon.ha.jdbc;

import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by TIANSHOUZHI336 on 2016/12/4.
 */
public class TransactionTest extends BaseTest{
    @Test
    public void testRwSplit() throws SQLException {
        Connection connection = dragonHADatasource.getConnection();
        connection.setAutoCommit(false);
        try{
            PreparedStatement insert1 = connection.prepareStatement("INSERT into user(name) VALUES ('huhuamin')");
            PreparedStatement insert2 = connection.prepareStatement("INSERT into user(name) VALUES ('wangxiaoxiao')");
            insert1.execute();
            insert2.execute();
            connection.commit();
        }catch (Exception e){
            e.printStackTrace();
            connection.rollback();
        }
    }
}
