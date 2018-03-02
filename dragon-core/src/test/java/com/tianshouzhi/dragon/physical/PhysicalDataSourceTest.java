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

    //测试最小驱逐时间
	@Test
	public void testMinEvictableIdleTime() throws InterruptedException {
		PhysicalDataSource dataSource = new PhysicalDataSource();
		dataSource.setUsername("root");
		dataSource.setPassword("");
		dataSource.setUrl("jdbc:mysql://localhost:3306/test?useSSL=false");
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        int initPoolSize = 5;
        int maxPoolSize = 10;
        int minPoolSize = 2;
        dataSource.setInitPoolSize(initPoolSize);
        dataSource.setMaxPoolSize(maxPoolSize);
        dataSource.setMinPoolSize(minPoolSize);
		dataSource.setTestWhenIdle(true);
		dataSource.setMinEvictableIdleTime(1000 * maxPoolSize);// 链接最大存活时间为10秒
		dataSource.setValidationInterval(1000);// 1秒检测一次
		dataSource.setCheckConnectionTimeout(1000);
		dataSource.setValidationTimeout(1);
		dataSource.init();

		int busyCount = dataSource.getBusyCount();
		int idleCount = dataSource.getIdleCount();
		int totalCount = dataSource.getTotalCount();
		assert busyCount == 0;
		assert idleCount == initPoolSize;
		assert totalCount == initPoolSize;

		Thread.sleep(1000 * 15);

		busyCount = dataSource.getBusyCount();
		idleCount = dataSource.getIdleCount();
		totalCount = dataSource.getTotalCount();
		assert busyCount == 0;
		assert idleCount == minPoolSize;
		assert totalCount == minPoolSize;
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
        final PhysicalDataSource dataSource = new PhysicalDataSource();
		String username = "root";
		dataSource.setUsername(username);
		String password = "shxx12151022";
		dataSource.setPassword(password);
		String url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=false";
		dataSource.setUrl(url);
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");

        ArrayList<String> connectionInitSqls = new ArrayList<>();
        connectionInitSqls.add("set names utf8mb4");//单位毫秒
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
