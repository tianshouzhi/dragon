package com.tianshouzhi.dragon.ha.jdbc;

import com.tianshouzhi.dragon.ha.config.DragonHADatasourceBuilder;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import com.tianshouzhi.dragon.ha.jdbc.datasource.DragonHADatasource;
import org.junit.After;
import org.junit.Before;

import java.sql.SQLException;

/**
 * Created by TIANSHOUZHI336 on 2016/12/8.
 */
public abstract class BaseTest {
    DragonHADatasource dragonHADatasource;
    DragonHAConnection connection;
    @Before
    public void init() throws Exception {
         dragonHADatasource= new DragonHADatasourceBuilder().build("dragon-ha-config.xml");
         connection= (DragonHAConnection) dragonHADatasource.getConnection();
    }

    @After
    public void tearDown() throws SQLException {
        if(connection!=null)
        connection.close();
    }
}
