package com.tianshouzhi.dragon.physical;

import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by tianshouzhi on 2018/3/2.
 */
public class PhysicalDataSourceTest {
	
	@Test
	public void testInitParam() throws SQLException {
		DragonDataSource dataSource = new DragonDataSource();
		dataSource.setUsername("dragon_ha");
		dataSource.setPassword("dragon_ha");
		dataSource.setUrl("jdbc:mysql://localhost:3306/test?useSSL=false");
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setInitPoolSize(5);

		dataSource.init();

		assert dataSource.getPoolSize() == 5;
		assert dataSource.getIdleSize() == 5;
		assert dataSource.getBusySize() == 0;
	}

	@Test
	public void testGetAndCloseConnection() throws SQLException {
		DragonDataSource dataSource = new DragonDataSource();
		dataSource.setUsername("dragon_ha");
		dataSource.setPassword("dragon_ha");
		dataSource.setUrl("jdbc:mysql://localhost:3306/test?useSSL=false");
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setInitPoolSize(5);

		dataSource.init();

		assert dataSource.getPoolSize() == 5;
		assert dataSource.getIdleSize() == 5;
		assert dataSource.getBusySize() == 0;

		Connection connection = dataSource.getConnection();

		assert dataSource.getPoolSize() == 5;
		assert dataSource.getIdleSize() == 4;
		assert dataSource.getBusySize() == 1;

		connection.close();

		assert dataSource.getPoolSize() == 5;
		assert dataSource.getIdleSize() == 5;
		assert dataSource.getBusySize() == 0;

	}

	@Test
	public void testCheckoutTime1() throws SQLException {
		DragonDataSource dataSource = new DragonDataSource();
		dataSource.setUsername("dragon_ha");
		dataSource.setPassword("dragon_ha");
		dataSource.setUrl("jdbc:mysql://localhost:3306/test?useSSL=false");
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setInitPoolSize(1);
		dataSource.setMinPoolSize(1);
		dataSource.setMaxPoolSize(1);
		dataSource.setCheckoutTimeout(1000);
		dataSource.init();

		dataSource.getConnection(); //success
		dataSource.getConnection(); //error
	}

    //测试最小驱逐时间
	@Test
	public void testMinEvictableIdleTime() throws InterruptedException, SQLException {
		DragonDataSource dataSource = new DragonDataSource();
		dataSource.setUsername("dragon_ha");
		dataSource.setPassword("dragon_ha");
		dataSource.setUrl("jdbc:mysql://localhost:3306/test?useSSL=false");
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setTestWhenIdle(true);
		dataSource.setInitPoolSize(5);
		dataSource.setMinPoolSize(2);
		dataSource.setMinEvictableIdleTime(1000 * 10);// 最小空闲时间，即一个链接空闲10s后才能被驱逐
		dataSource.setValidationInterval(1000); //默认5分钟检测一次，这里改成1ms
		dataSource.init();

		Thread.sleep(1000 * 15);

		assert dataSource.getPoolSize() == 2;
		assert dataSource.getIdleSize() == 2;
		assert dataSource.getBusySize() == 0;
	}
	
	/*
	测试init sql
	 CREATE TABLE `user` ( `id` int(11) DEFAULT NULL, `name` varchar(255) DEFAULT NULL )
	 ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

	 ALTER TABLE user CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
	 ALTER DATABASE test CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
	 */
	//Incorrect string value: '\xF0\x9F\x98\x84' for column 'name' at row 1
    @Test
    public void testInitSqls() throws SQLException {
        final DragonDataSource dataSource = new DragonDataSource();
		dataSource.setUsername("root");
		dataSource.setPassword("shxx12151022");
		dataSource.setUrl("jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=false");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");

        ArrayList<String> connectionInitSqls = new ArrayList<>();
        connectionInitSqls.add("set names utf8mb4");
        dataSource.setConnectionInitSqls(connectionInitSqls);

		dataSource.init();

		Connection connection = dataSource.getConnection();

		int unicode = 0x1F604;
		String emoji= new String(Character.toChars(unicode));
		PreparedStatement statement=connection.prepareStatement("insert into user(name) values(?)");
		statement.setString(1,emoji);
		statement.executeUpdate();
    }
}
