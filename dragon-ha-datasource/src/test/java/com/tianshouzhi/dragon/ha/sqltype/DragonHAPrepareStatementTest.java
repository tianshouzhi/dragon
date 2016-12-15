package com.tianshouzhi.dragon.ha.sqltype;

import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import org.junit.Assert;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by TIANSHOUZHI336 on 2016/12/7.
 */
public class DragonHAPrepareStatementTest extends BaseTest{
    @Test
    public void testInsert() throws SQLException {
        DragonHAConnection connection = this.connection;
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT  INTO  user(name) VALUES (?)");
        preparedStatement.setString(1,"tianshouzhi");
        int i = preparedStatement.executeUpdate();
        Assert.assertEquals("insert success",i,1);
    }
    @Test
    public void testDelete() throws SQLException {
        DragonHAConnection connection = this.connection;
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM USER WHERE id=?");
        preparedStatement.setInt(1,2);
        int i = preparedStatement.executeUpdate();
        Assert.assertTrue(i<=1);
    }
    @Test
    public void testUpdate() throws SQLException {
        DragonHAConnection connection = this.connection;
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE USER SET name=? WHERE id=?");
        preparedStatement.setString(1,"wangxiaoxiao");
        preparedStatement.setInt(2,3);
        int i = preparedStatement.executeUpdate();
        Assert.assertTrue(i<=1);
    }
    @Test
    public void testQuery() throws SQLException {
        DragonHAConnection connection = this.connection;
        PreparedStatement preparedStatement = connection.prepareStatement("/*DRAGON_HA( DBINDEXES =dragon_ha_master)*/SELECT * from USER");
        preparedStatement.executeQuery();
        ResultSet resultSet = preparedStatement.getResultSet();
        while (resultSet.next()){
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            System.out.println("id:"+id+",name:"+name);
        }
    }
    @Test
    public void mixTest() throws SQLException {
        DragonHAConnection connection = this.connection;
        PreparedStatement preparedStatement = connection.prepareStatement("/*DRAGON_HA( DBINDEXES =dragon_ha_master)*/SELECT * from USER");
        preparedStatement.executeQuery();
        ResultSet resultSet = preparedStatement.getResultSet();
        while (resultSet.next()){
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            System.out.println("id:"+id+",name:"+name);
        }

        preparedStatement = connection.prepareStatement("INSERT  INTO  user(name) VALUES (?)");
        preparedStatement.setString(1,"huhuamin");
        int i = preparedStatement.executeUpdate();
        Assert.assertEquals("insert success",i,1);

        preparedStatement = connection.prepareStatement("/*DRAGON_HA( DBINDEXES =dragon_ha_master)*/SELECT * from USER");
        preparedStatement.executeQuery();
        resultSet = preparedStatement.getResultSet();
        while (resultSet.next()){
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            System.out.println("id:"+id+",name:"+name);
        }

        preparedStatement = connection.prepareStatement("UPDATE USER SET name=?");
        preparedStatement.setString(1,"wanghanao");
        preparedStatement.executeUpdate();

        preparedStatement = connection.prepareStatement("/*DRAGON_HA( DBINDEXES =dragon_ha_slave1)*/SELECT * from USER");
        preparedStatement.executeQuery();
        resultSet = preparedStatement.getResultSet();
        while (resultSet.next()){
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            System.out.println("id:"+id+",name:"+name);
        }

    }
    @Test
    public void testBatch() throws SQLException {
        DragonHAConnection connection = this.connection;
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT  INTO  user(name) VALUES (?)");
        preparedStatement.setString(1,"wangxiaoxiao2");
        preparedStatement.addBatch();
        preparedStatement.setString(1,"wanghanhao2");
        preparedStatement.addBatch();
        preparedStatement.setString(1,"huhuamin2");
        preparedStatement.addBatch();
        preparedStatement.addBatch("INSERT INTO USER(name) VALUES ('xxxxxxx2')"); //混合使用两种模式会报错
        int[] ints = preparedStatement.executeBatch();
        System.out.println(ints);
        testQuery();

    }
}
