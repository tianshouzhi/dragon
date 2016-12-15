package com.tianshouzhi.dragon.ha.sqltype;

import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by TIANSHOUZHI336 on 2016/12/7.
 */
public class InvalidTest extends BaseTest{
    @Test
    public void test() {
        int i=0;
        while(i<30){
            try{
                DragonHAConnection connection = dragonHADatasource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user ");
                ResultSet resultSet = preparedStatement.executeQuery();
                i++;
                while(resultSet.next()){
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    System.out.println(i+","+connection.getDbIndex()+",id:" + id + ",name:" + name);
                }
                resultSet.close();
                preparedStatement.close();
                connection.close();
//                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}
