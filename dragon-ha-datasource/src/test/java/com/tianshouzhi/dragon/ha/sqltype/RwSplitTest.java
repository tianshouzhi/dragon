package com.tianshouzhi.dragon.ha.sqltype;

import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.jdbc.Driver;
import com.tianshouzhi.dragon.ha.dbselector.DatasourceWrapper;
import com.tianshouzhi.dragon.ha.jdbc.DragonHADatasource;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 读写分离测试
 */
public class RwSplitTest {
    @Test
    public void testRwSplit() throws SQLException {
        List<DatasourceWrapper> list=new ArrayList<DatasourceWrapper>();
        DragonHADatasource dragonHADatasource =new DragonHADatasource(list);
        DruidDataSource master=new DruidDataSource();
        master.setUsername("root");
        master.setPassword("shxx12151022");
        master.setDriverClassName(Driver.class.getName());
        master.setUrl("jdbc:mysql://localhost:3306/tddl_master");
        list.add(new DatasourceWrapper("tddl_master",0,10,master));

        DruidDataSource slave=new DruidDataSource();
        slave.setUsername("root");
        slave.setPassword("shxx12151022");
        slave.setDriverClassName(Driver.class.getName());
        slave.setUrl("jdbc:mysql://localhost:3306/tddl_slave");
        list.add(new DatasourceWrapper("tddl_slave",10,0,slave));

        Connection connection = dragonHADatasource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user ");
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            int id = resultSet.getInt("user_id");
            String tag_name = resultSet.getString("username");
            System.out.println("user_id:"+id+",username:"+tag_name);
        }
        preparedStatement = connection.prepareStatement("SELECT * FROM user ");
        resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            int id = resultSet.getInt("user_id");
            String tag_name = resultSet.getString("username");
            System.out.println("user_id:"+id+",username:"+tag_name);
        }
        PreparedStatement insert = connection.prepareStatement("INSERT into user(user_id,username) VALUES (1007,'wangxiaoxiao')");
        int i = insert.executeUpdate();
        preparedStatement = connection.prepareStatement("SELECT * FROM user ");
        resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            int id = resultSet.getInt("user_id");
            String tag_name = resultSet.getString("username");
            System.out.println("user_id:"+id+",username:"+tag_name);
        }
    }
}
