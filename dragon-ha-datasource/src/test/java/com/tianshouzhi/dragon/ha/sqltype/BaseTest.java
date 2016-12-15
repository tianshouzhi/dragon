package com.tianshouzhi.dragon.ha.sqltype;

import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.jdbc.Driver;
import com.tianshouzhi.dragon.ha.dbselector.DatasourceWrapper;
import com.tianshouzhi.dragon.ha.jdbc.DragonHADatasource;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import org.junit.After;
import org.junit.Before;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TIANSHOUZHI336 on 2016/12/8.
 */
public abstract class BaseTest {
    DragonHADatasource dragonHADatasource;
    DragonHAConnection connection;
    @Before
    public void init() throws SQLException {
        List<DatasourceWrapper> list=new ArrayList<DatasourceWrapper>();
        DruidDataSource master=new DruidDataSource();
        master.setUsername("root");
        master.setPassword("shxx12151022");
        master.setDriverClassName(Driver.class.getName());
        master.setUrl("jdbc:mysql://localhost:3306/dragon_ha_master?useSSL=false");
        list.add(new DatasourceWrapper("dragon_ha_master",0,10,master));

        DruidDataSource slave=new DruidDataSource();
        slave.setUsername("root");
        slave.setPassword("shxx12151022");
        slave.setDriverClassName(Driver.class.getName());
        slave.setUrl("jdbc:mysql://localhost:3306/dragon_ha_slave1?useSSL=false");
        list.add(new DatasourceWrapper("dragon_ha_slave1",10,0,slave));

        DruidDataSource slave2=new DruidDataSource();
        slave2.setUsername("root");
        slave2.setPassword("shxx12151022");
        slave2.setDriverClassName(Driver.class.getName());
        slave2.setUrl("jdbc:mysql://localhost:3306/dragon_ha_slave2?useSSL=false");
        list.add(new DatasourceWrapper("dragon_ha_slave2",10,0,slave2));
        dragonHADatasource =new DragonHADatasource(list);
        connection= (DragonHAConnection) dragonHADatasource.getConnection();
    }

    @After
    public void tearDown() throws SQLException {
        if(connection!=null)
        connection.close();
    }
}
