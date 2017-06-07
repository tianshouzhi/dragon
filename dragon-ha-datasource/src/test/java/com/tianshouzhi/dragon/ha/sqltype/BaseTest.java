package com.tianshouzhi.dragon.ha.sqltype;

import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.jdbc.Driver;
import com.tianshouzhi.dragon.ha.config.DragonHADatasourceBuilder;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import com.tianshouzhi.dragon.ha.jdbc.datasource.DragonHADatasource;
import com.tianshouzhi.dragon.ha.jdbc.datasource.dbselector.DatasourceWrapper;
import org.junit.After;
import org.junit.Before;

import javax.sql.DataSource;
import java.io.IOException;
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
