package com.tianshouzhi.dragon.demo.ha.sqltype;

import com.tianshouzhi.dragon.demo.ha.jdbc.connection.DragonHAConnection;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 查询3000次，查看每个从库选择的比重
 */
public class DBSelectorTest extends BaseTest{

    @Test
    public void selectProbilityTest() {
        Map<String,AtomicInteger> map=new HashMap<String, AtomicInteger>();
        int i=0;
        while(i<3000){
            try{
                DragonHAConnection connection = (DragonHAConnection) dragonHADatasource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user ");
                ResultSet resultSet = preparedStatement.executeQuery();
                i++;
                while(resultSet.next()){
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String dataSourceIndex = connection.getDataSourceIndex();
                    AtomicInteger atomicInteger = map.get(dataSourceIndex);
                    if(atomicInteger==null){
                        atomicInteger=new AtomicInteger();
                    }
                    atomicInteger.incrementAndGet();
                    map.put(dataSourceIndex,atomicInteger);
                    System.out.println(i+","+ dataSourceIndex +",id:" + id + ",name:" + name);
                }
                resultSet.close();
                preparedStatement.close();
                connection.close();
//                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println(map);
    }
}
