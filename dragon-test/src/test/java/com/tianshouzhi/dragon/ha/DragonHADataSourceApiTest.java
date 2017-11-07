package com.tianshouzhi.dragon.ha;

import com.alibaba.druid.pool.DruidDataSource;
import com.tianshouzhi.dragon.ha.hint.DragonHAHintUtil;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import com.tianshouzhi.dragon.ha.jdbc.datasource.DragonHADatasource;
import com.tianshouzhi.dragon.ha.jdbc.statement.DragonHAPrepareStatement;
import org.junit.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tianshouzhi on 2017/11/7.
 */
public class DragonHADataSourceApiTest {
    static DataSource datasource;
    static Connection connection;

    @BeforeClass
    public static void beforeClass() throws Exception {
        DragonHADatasource dragonHADatasource = new DragonHADatasource();
        DruidDataSource master = createRealDataSource();
        DruidDataSource slave1 = createRealDataSource();
        DruidDataSource slave2 = createRealDataSource();
        dragonHADatasource.addRealDatasource("master", 0, 10, master);
        dragonHADatasource.addRealDatasource("slave1", 10, 0, slave1);
        dragonHADatasource.addRealDatasource("slave2", 10, 0, slave2);
        dragonHADatasource.init();
        datasource = dragonHADatasource;
    }

    @Before
    public void before() throws SQLException {
        connection = datasource.getConnection();
    }

    private static DruidDataSource createRealDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("shxx12151022");
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/test");
        return druidDataSource;
    }

    @After
    public void after() throws SQLException {
        if (connection != null)
            connection.close();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        if (datasource != null)
            ((DragonHADatasource) datasource).close();
    }

    @Test
    public void testBuildFromLocalConfig() throws SQLException {
        DragonHADatasource datasource=new DragonHADatasource();
        datasource.setLocalConfigFile("dragon-ha.properties");
        datasource.init();
        Connection connection = datasource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT  into USER (name) VALUES ('tianshouzhi')");
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
        datasource.close();
    }

    @Test
    public void testInsert() throws SQLException {
        Statement statement = connection.createStatement();
        int insertCount = statement.executeUpdate("INSERT INTO user(name) VALUES ('tianshouzhi')");
        assert insertCount == 1;
        statement.close();

        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO user(name) VALUES (?)");
        preparedStatement.setString(1, "wangxiaoxiao");
        int insertCount1 = preparedStatement.executeUpdate();
        assert insertCount1 == 1;
        preparedStatement.close();
    }

    @Test
    public void testSelect() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM user ");

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            System.out.println("id:" + id + ",name:" + name);
        }
        resultSet.close();
        statement.close();
    }

    @Test
    public void testUpdate() throws SQLException {
        Statement statement = connection.createStatement();
        int updateCount = statement.executeUpdate("UPDATE user SET name='tianshouzhi' WHERE id<=10");
        assert updateCount <= 11;

        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE user SET name=? WHERE id=?");
        preparedStatement.setString(1, "wangxiaoxiao");
        preparedStatement.setInt(2, 4);
        int i = preparedStatement.executeUpdate();
        Assert.assertTrue(i <= 1);
    }

    @Test
    public void testDelete() throws SQLException {
        Statement statement = connection.createStatement();
        int deleteCount = statement.executeUpdate("DELETE FROM user WHERE id>10");
        System.out.println(deleteCount);

        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM user WHERE id=?");
        preparedStatement.setInt(1, 16);
        int i = preparedStatement.executeUpdate();
        Assert.assertTrue(i <= 1);
    }

    @Test
    public void testAutoGenerateKeys() throws SQLException {
        Statement statement = connection.createStatement();
        int insertCount = statement.executeUpdate("INSERT INTO user(name) VALUES ('luyang')", Statement.RETURN_GENERATED_KEYS);
        assert insertCount == 1;
        ResultSet generatedKeys = statement.getGeneratedKeys();
        int generatedKey = 0;
        while (generatedKeys.next()) {
            generatedKey = generatedKeys.getInt(1);
        }
        generatedKeys.close();
        statement.close();

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT @@identity AS id");
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int id = resultSet.getInt("id");

        assert generatedKey == id;

        resultSet.close();
        preparedStatement.close();

    }

    @Test
    public void testReuseStatement(){

    }

    @Test
    public void testStatementBatch() throws SQLException {
        Statement statement = connection.createStatement();
        statement.addBatch("INSERT INTO user(name) VALUES ('wanghanhao'),('huhuamin')");
        statement.addBatch("INSERT INTO user(name) VALUES ('luyang')");
        int[] ints = statement.executeBatch();
        assert ints.length == 2;
        assert ints[0] == 2;
        assert ints[1] == 1;
    }

    // 混合使用PreparedStatement 和 preparedStatement两种batch
    @Test
    public void testPreparedStatementBatch() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT  INTO  user(name) VALUES (?)");
        preparedStatement.setString(1, "wangxiaoxiao2");
        preparedStatement.addBatch();
        preparedStatement.setString(1, "wanghanhao2");
        preparedStatement.addBatch();
        preparedStatement.setString(1, "huhuamin2");
        preparedStatement.addBatch();
        preparedStatement.addBatch("INSERT INTO USER(name) VALUES ('xxxxxxx2')");
        int[] ints = preparedStatement.executeBatch();
        assert ints.length == 2;
        assert ints[0] == 3;
        assert ints[1] == 1;
    }

    @Test
    public void testTransaction() throws SQLException {
        connection.setAutoCommit(false);
        try {
            PreparedStatement insert1 = connection.prepareStatement("SELECT * FROM user");
            String realDSName = ((DragonHAConnection) connection).getRealDSName();
            assert "master" == realDSName;

            PreparedStatement insert2 = connection.prepareStatement("INSERT INTO user(name) VALUES ('wangxiaoxiao')");
            insert1.execute();
            insert2.execute();
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
        }
    }

    @Test
    public void testRollback() throws SQLException {
        connection.setAutoCommit(false);
        try {
            PreparedStatement insert1 = connection.prepareStatement("INSERT INTO user(name) VALUES ('huhuamin')");
            PreparedStatement insert2 = connection.prepareStatement("INSERT INTO user(name) VALUES ('wangxiaoxiao')");
            insert1.execute();
            int i = 1 / 0;
            insert2.execute();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            connection.rollback();
        }
    }

    @Test
    public void testRWSplit() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user ");
        preparedStatement.execute();
        String realDSName = ((DragonHAConnection) connection).getRealDSName();
        assert Arrays.asList("slave1", "slave2").contains(realDSName);
        preparedStatement.close();

        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO user(name) VALUES ('huhuamin')");

        realDSName = ((DragonHAConnection) connection).getRealDSName();
        statement.close();

        assert "master".equals(realDSName);

        connection.createStatement().execute("SELECT * FROM user");
        assert "master".equals(realDSName);
    }

    @Test
    public void testApiHint() throws SQLException {
        DragonHAHintUtil.forceMaster();
        Statement statement = connection.createStatement();
        statement.executeQuery("SELECT * FROM user ");

        String realDSName = ((DragonHAConnection) connection).getRealDSName();
        assert "master" == realDSName;

        DragonHAHintUtil.clear();
    }

    @Test
    public void testSqlHint() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeQuery("/*master*/ SELECT * FROM user ");

        String realDSName = ((DragonHAConnection) connection).getRealDSName();
        assert "master" == realDSName;
    }

    /**
     * 查询3000次，查看每个从库选择的比重
     */
    @Test
    public void selectProbilityTest() {
        Map<String, AtomicInteger> map = new HashMap<String, AtomicInteger>();
        int i = 0;
        while (i < 3000) {
            try {
                DragonHAConnection connection = (DragonHAConnection) datasource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user ");
                ResultSet resultSet = preparedStatement.executeQuery();
                String dataSourceIndex = connection.getRealDSName();
                AtomicInteger atomicInteger = map.get(dataSourceIndex);
                if (atomicInteger == null) {
                    atomicInteger = new AtomicInteger();
                }
                atomicInteger.incrementAndGet();
                map.put(dataSourceIndex, atomicInteger);
                i++;
                resultSet.close();
                preparedStatement.close();
                connection.close();
                // Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println(map);
    }
}
