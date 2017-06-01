package com.tianshouzhi.dragon.demo.ha.sqltype;

import com.tianshouzhi.dragon.demo.ha.hint.ThreadLocalHintUtil;
import com.tianshouzhi.dragon.demo.ha.jdbc.connection.DragonHAConnection;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * Created by TIANSHOUZHI336 on 2016/12/7.
 */
public class DragonHAStatementTest extends BaseTest{
    @Test
    public void testAutoGenerateKeys() throws SQLException {
        DragonHAConnection connection = this.connection;
        Statement statement = connection.createStatement();
        boolean result = statement.execute("INSERT INTO user(name) VALUES ('luyang')",Statement.RETURN_GENERATED_KEYS);
        if(result){
            ResultSet resultSet = statement.getResultSet();
            System.out.println(result);
        }else{
            ResultSet generatedKeys = statement.getGeneratedKeys();
            while (generatedKeys.next()){
                int anInt = generatedKeys.getInt(1);
                System.out.println(anInt);
            }
            int updateCount = statement.getUpdateCount();
            System.out.println(generatedKeys);
            System.out.println(updateCount);
        }
    }
    @Test
    public void testDelete() throws SQLException {
        DragonHAConnection connection = this.connection;
        Statement statement = connection.createStatement();
        int i = statement.executeUpdate("DELETE from USER where id>10");
        System.out.println(i);
    }
    @Test
    public void testQuery() throws SQLException {
        ThreadLocalHintUtil.setDBIndexes("dragon_ha_master");
        DragonHAConnection connection = this.connection;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM USER ");
        while (resultSet.next()){
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            System.out.println("id:" + id + ",name:" + name);
        }
        ThreadLocalHintUtil.remove();
    }
    @Test
    public void testUpdate() throws SQLException {
        testQuery();
        ThreadLocalHintUtil.setDBIndexes("dragon_ha_master");
        DragonHAConnection connection = this.connection;
        Statement statement = connection.createStatement();
        int i = statement.executeUpdate("UPDATE user SET name='tianshouzhi' where id<=10");
        System.out.println(i);
        testQuery();
        ThreadLocalHintUtil.remove();
    }

    @Test
    public void testBatch() throws SQLException {
        DragonHAConnection connection = this.connection;
        Statement statement = connection.createStatement();
        statement.addBatch("insert into user(name) VALUES ('wanghanhao'),('huhuamin')");
        statement.addBatch("insert into user(name) VALUES ('luyang')");
        int[] ints = statement.executeBatch();
        System.out.println(Arrays.toString(ints));
    }
}
