package com.tianshouzhi.dragon.ha.jdbc;

import com.tianshouzhi.dragon.ha.jdbc.BaseTest;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 读写分离测试，主要观察log中的连接切换
 */
public class RwSplitTest extends BaseTest {
    @Test
    public void testRwSplit() throws SQLException {
        Connection connection = dragonHADatasource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user ");
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            int id = resultSet.getInt("id");
            String tag_name = resultSet.getString("name");
            System.out.println("id:"+id+",name:"+tag_name);
        }
        preparedStatement = connection.prepareStatement("SELECT * FROM user ");
        resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            int id = resultSet.getInt("id");
            String tag_name = resultSet.getString("name");
            System.out.println("id:"+id+",name:"+tag_name);
        }
        PreparedStatement insert = connection.prepareStatement("INSERT into user(id,name) VALUES (1013,'wangxiaoxiao')");
        int i = insert.executeUpdate();
        preparedStatement = connection.prepareStatement("SELECT * FROM user ");
        resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            int id = resultSet.getInt("id");
            String tag_name = resultSet.getString("name");
            System.out.println("id:"+id+",name:"+tag_name);
        }
    }
}
