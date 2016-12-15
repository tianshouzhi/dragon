package com.tianshouzhi.dragon.ha.sqltype;

import com.tianshouzhi.dragon.ha.dbselector.DBIndex;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
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
        Map<DBIndex,AtomicInteger> map=new HashMap<DBIndex, AtomicInteger>();
        int i=0;
        while(i<3000){
            try{
                DragonHAConnection connection = dragonHADatasource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user ");
                ResultSet resultSet = preparedStatement.executeQuery();
                i++;
                while(resultSet.next()){
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    DBIndex dbIndex = connection.getDbIndex();
                    AtomicInteger atomicInteger = map.get(dbIndex);
                    if(atomicInteger==null){
                        atomicInteger=new AtomicInteger();
                    }
                    atomicInteger.incrementAndGet();
                    map.put(dbIndex,atomicInteger);
                    System.out.println(i+","+ dbIndex +",id:" + id + ",name:" + name);
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
