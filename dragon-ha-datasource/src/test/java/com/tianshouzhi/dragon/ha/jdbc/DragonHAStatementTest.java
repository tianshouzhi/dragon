package com.tianshouzhi.dragon.ha.jdbc;

import com.tianshouzhi.dragon.ha.hint.DragonHAHintUtil;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import com.tianshouzhi.dragon.ha.jdbc.statement.DragonHAStatement;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * Created by TIANSHOUZHI336 on 2016/12/7.
 */
public class DragonHAStatementTest extends BaseTest {
	@Test
	public void testAutoGenerateKeys() throws SQLException {
		DragonHAConnection connection = this.connection;
		Statement statement = connection.createStatement();
		statement.execute("INSERT INTO user(name) VALUES ('luyang')", Statement.RETURN_GENERATED_KEYS);
		ResultSet generatedKeys = statement.getGeneratedKeys();
		int generatedKey = 0;
		while (generatedKeys.next()) {
			generatedKey = generatedKeys.getInt(1);
		}

		DragonHAStatement statement1 = connection.createStatement();
		ResultSet resultSet = statement1.executeQuery("select @@identity as id");
		resultSet.next();
		int id = resultSet.getInt("id");
		assert generatedKey == id;
		System.out.println("generatedKey==id==" + id);
	}

	@Test
	public void testDelete() throws SQLException {
		DragonHAConnection connection = this.connection;
		Statement statement = connection.createStatement();
		int i = statement.executeUpdate("DELETE from user where id>10");
		System.out.println(i);
	}

	@Test
	public void testQuery() throws SQLException {
		DragonHAHintUtil.setHintMaster(true);
		DragonHAConnection connection = this.connection;
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM user ");
		while (resultSet.next()) {
			int id = resultSet.getInt("id");
			String name = resultSet.getString("name");
			System.out.println("id:" + id + ",name:" + name);
		}
		DragonHAHintUtil.clearHintMaster();
	}

	@Test
	public void testUpdate() throws SQLException {
		testQuery();
		DragonHAConnection connection = this.connection;
		Statement statement = connection.createStatement();
		int i = statement.executeUpdate("UPDATE user SET name='tianshouzhi' where id<=10");
		System.out.println(i);
		testQuery();
	}

	@Test
	public void testBatch() throws SQLException {
		DragonHAConnection connection = this.connection;
		Statement statement = connection.createStatement();
		statement.addBatch("insert into user(name) VALUES ('wanghanhao'),('huhuamin')");
		statement.addBatch("insert into user(name) VALUES ('luyang')");
		int[] ints = statement.executeBatch();
		System.out.println(Arrays.toString(ints));
	}

	@Test
	public void testCreateTable() throws SQLException {
		DragonHAConnection connection = this.connection;
		Statement statement = connection.createStatement();
		int update = statement.executeUpdate("create table test(id int,name varchar(255))");
		System.out.println(update);
	}

	@Test
	public void testReuse() {

	}
}
