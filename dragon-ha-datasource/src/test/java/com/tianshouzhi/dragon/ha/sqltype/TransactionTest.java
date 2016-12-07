package com.tianshouzhi.dragon.ha.sqltype;

import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.jdbc.Driver;
import com.tianshouzhi.dragon.ha.dbselector.DatasourceWrapper;
import com.tianshouzhi.dragon.ha.jdbc.DragonHADatasource;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TIANSHOUZHI336 on 2016/12/4.
 */
public class TransactionTest {
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
        connection.setAutoCommit(false);
        try{
            PreparedStatement insert1 = connection.prepareStatement("INSERT into user(user_id,username) VALUES (26,'huhuamin')");
            PreparedStatement insert2 = connection.prepareStatement("INSERT into user(user_id,username) VALUES (25,'wangxiaoxiao')");
            insert1.execute();
            insert2.execute();
            connection.commit();
        }catch (Exception e){
            e.printStackTrace();
            connection.rollback();
        }
    }
}
