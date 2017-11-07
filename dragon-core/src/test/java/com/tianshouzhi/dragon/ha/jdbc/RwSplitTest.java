package com.tianshouzhi.dragon.ha.jdbc;

import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 读写分离测试，主要观察log中的连接切换
 */
public class RwSplitTest extends BaseTest {
	@Test
	public void testRwSplit() throws SQLException {
		DragonHAConnection connection = (DragonHAConnection) dragonHADatasource.getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user ");
		preparedStatement.executeQuery();
		assert !"master".equals(connection.getRealDSName());

		preparedStatement = connection.prepareStatement("SELECT * FROM user ");
		preparedStatement.executeQuery();
		assert !"master".equals(connection.getRealDSName());

		PreparedStatement insert = connection.prepareStatement("INSERT into user(id,name) VALUES (1012," +
				"'wangxiaoxiao')");
		insert.executeUpdate();
		assert "master".equals(connection.getRealDSName());

		preparedStatement = connection.prepareStatement("SELECT * FROM user ");
		preparedStatement.executeQuery();
		assert "master".equals(connection.getRealDSName());

	}
}
