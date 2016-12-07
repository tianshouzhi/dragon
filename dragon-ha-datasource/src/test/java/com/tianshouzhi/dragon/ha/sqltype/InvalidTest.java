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
 * Created by TIANSHOUZHI336 on 2016/12/7.
 */
public class InvalidTest {
    @Test
    public void test() throws SQLException {
        List<DatasourceWrapper> list = new ArrayList<DatasourceWrapper>();
        DragonHADatasource dragonHADatasource = new DragonHADatasource(list);
        DruidDataSource master = new DruidDataSource();
        master.setUsername("root");
        master.setPassword("shxx12151022");
        master.setDriverClassName(Driver.class.getName());
        master.setUrl("jdbc:mysql://localhost:3306/tddl_master");
        list.add(new DatasourceWrapper("tddl_master", 0, 10, master));

        DruidDataSource slave = new DruidDataSource();
        slave.setUsername("root");
        slave.setPassword("shxx12151022");
        slave.setDriverClassName(Driver.class.getName());
        slave.setUrl("jdbc:mysql://localhost:3306/tddl_slave");
        list.add(new DatasourceWrapper("tddl_slave", 10, 0, slave));

        Connection connection = dragonHADatasource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user ");
        boolean goon=true;
        do{
            try {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int id = resultSet.getInt("user_id");
                    String tag_name = resultSet.getString("username");
                    System.out.println("user_id:" + id + ",username:" + tag_name);
                }
                goon=false;
            } catch (Exception e) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }while (goon);

    }
}
