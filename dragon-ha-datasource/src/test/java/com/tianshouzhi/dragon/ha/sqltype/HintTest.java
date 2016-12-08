package com.tianshouzhi.dragon.ha.sqltype;

import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by TIANSHOUZHI336 on 2016/12/4.
 */
public class HintTest extends BaseTest{
    @Test
    public void testRwSplit() throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user ");
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            int id = resultSet.getInt("user_id");
            String tag_name = resultSet.getString("username");
            System.out.println("user_id:"+id+",username:"+tag_name);
        }
        preparedStatement = connection.prepareStatement("/*DRAGON_HA ( DBINDEXES = tddl_master )*/ SELECT * FROM user ");

        resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            int id = resultSet.getInt("user_id");
            String tag_name = resultSet.getString("username");
            System.out.println("user_id:"+id+",username:"+tag_name);
        }
        /*PreparedStatement insert = connection.prepareStatement("INSERT into user(user_id,username) VALUES (2,'wangxiaoxiao')");
        int i = insert.executeUpdate();
        insert.close();*/
    }
}
