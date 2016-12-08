package com.tianshouzhi.dragon.ha.sqltype;

import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
    public void testDelete(){

    }
    @Test
    public void testUpdate(){

    }
    @Test
    public void testQuery(){

    }
    @Test
    public void testBatch(){

    }
}
