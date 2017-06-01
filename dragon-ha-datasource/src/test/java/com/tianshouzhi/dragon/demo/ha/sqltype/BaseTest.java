package com.tianshouzhi.dragon.demo.ha.sqltype;

import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.jdbc.Driver;
import com.tianshouzhi.dragon.demo.ha.jdbc.connection.DragonHAConnection;
import com.tianshouzhi.dragon.demo.ha.jdbc.datasource.DragonHADatasource;
import com.tianshouzhi.dragon.demo.ha.jdbc.datasource.dbselector.DatasourceWrapper;
import org.junit.After;
import org.junit.Before;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2016/12/8.
 */
public abstract class BaseTest {
    DragonHADatasource dragonHADatasource;
    DragonHAConnection connection;
    @Before
    public void init() throws SQLException {
        Map<String,DatasourceWrapper> map=new HashMap<String, DatasourceWrapper>();
        DruidDataSource master=new DruidDataSource();
        master.setUsername("root");
        master.setPassword("shxx12151022");
        master.setDriverClassName(Driver.class.getName());
        master.setUrl("jdbc:mysql://localhost:3306/dragon_ha_master?useSSL=false");
        map.put("dragon_ha_master",new DatasourceWrapper("dragon_ha_master",0,10,master));

        DruidDataSource slave=new DruidDataSource();
        slave.setUsername("root");
        slave.setPassword("shxx12151022");
        slave.setDriverClassName(Driver.class.getName());
        slave.setUrl("jdbc:mysql://localhost:3306/dragon_ha_slave1?useSSL=false");
        map.put("dragon_ha_slave1",new DatasourceWrapper("dragon_ha_slave1",10,0,slave));

        DruidDataSource slave2=new DruidDataSource();
        slave2.setUsername("root");
        slave2.setPassword("shxx12151022");
        slave2.setDriverClassName(Driver.class.getName());
        slave2.setUrl("jdbc:mysql://localhost:3306/dragon_ha_slave2?useSSL=false");
        map.put("dragon_ha_slave2",new DatasourceWrapper("dragon_ha_slave2",10,0,slave2));
        dragonHADatasource =new DragonHADatasource();
        dragonHADatasource.setIndexDsMap(map);
        connection= (DragonHAConnection) dragonHADatasource.getConnection();
    }

    @After
    public void tearDown() throws SQLException {
        if(connection!=null)
        connection.close();
    }
}
