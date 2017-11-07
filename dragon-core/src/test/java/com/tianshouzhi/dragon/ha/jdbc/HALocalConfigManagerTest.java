package com.tianshouzhi.dragon.ha.jdbc;

import com.tianshouzhi.dragon.ha.jdbc.datasource.DragonHADatasource;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by tianshouzhi on 2017/11/2.
 */
public class HALocalConfigManagerTest {
    @Test
    public void test() throws SQLException {
        DragonHADatasource datasource=new DragonHADatasource();
        datasource.setLocalConfigFile("dragon-ha.properties");
        datasource.init();
        Connection connection = datasource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT  into USER (name) VALUES ('tianshouzhi')");
        int i = preparedStatement.executeUpdate();
        System.out.println(i);
    }
}
