package com.tianshouzhi.dragon.sharding.jdbc.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.tianshouzhi.dragon.sharding.route.LogicDatabase;
import com.tianshouzhi.dragon.sharding.route.LogicTable;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TIANSHOUZHI336 on 2017/2/24.
 */
public class DragonShardingDataSourceTest {
    private static DataSource dataSource;

    @BeforeClass
    public static void before(){
        LogicDatabase logicDatabase = makeLogicDataSource();
        HashMap<String, LogicTable> logicTableMap = new HashMap<String, LogicTable>();
        LogicTable logicTable = makeLogicTable("user", logicDatabase);
        LogicTable account = makeLogicTable("user_account", logicDatabase);
        logicTableMap.put("user",logicTable);
        logicTableMap.put("user_account",account);
        dataSource=new DragonShardingDataSource(logicDatabase, logicTableMap);
    }

    @Test
    public void testBatchInsert() throws SQLException {
        String sql="insert into user(id,name) values(?,?),(?,?),(?,?),(?,?),(?,?),(?,?),(?,?),(?,?)";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,10000);
        preparedStatement.setString(2,"tianshouzhi");
        preparedStatement.setInt(3,20000);
        preparedStatement.setString(4,"wangxiaoxiao");
        preparedStatement.setInt(5,10001);
        preparedStatement.setString(6,"huhuamin");
        preparedStatement.setInt(7,20001);
        preparedStatement.setString(8,"wanghanao");
        preparedStatement.setInt(9,10100);
        preparedStatement.setString(10,"luyang");
        preparedStatement.setInt(11,20100);
        preparedStatement.setString(12,"chengkun");
        preparedStatement.setInt(13,10101);
        preparedStatement.setString(14,"tianhui");
        preparedStatement.setInt(15,20101);
        preparedStatement.setString(16,"tianmin");
        boolean execute = preparedStatement.execute();
        int updateCount = preparedStatement.getUpdateCount();
        System.out.println(updateCount);
    }

    @Test
    public void testBatchInsert1() throws SQLException {
        String sql="insert into user_account(user_id,account_no,money) values(?,?,?)";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,10000);
        preparedStatement.setString(2,"account_no_12344");
        preparedStatement.setDouble(3,20000);
        boolean execute = preparedStatement.execute();
        int updateCount = preparedStatement.getUpdateCount();
        System.out.println(updateCount);
    }

    @Test
    public void testBatchDelete() throws SQLException {
        String sql="delete from user where id in(?,?,?,?,?,?,?,?) ";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,10000);
        preparedStatement.setInt(2,20000);
        preparedStatement.setInt(3,10001);
        preparedStatement.setInt(4,20001);
        preparedStatement.setInt(5,10100);
        preparedStatement.setInt(6,20100);
        preparedStatement.setInt(7,10101);
        preparedStatement.setInt(8,20101);
        int updateCount = preparedStatement.executeUpdate();
        System.out.println("updateCount = " + updateCount);
    }

    @Test
    public void testUpdateWhereIdIn() throws SQLException {
        String sql="UPDATE  user SET NAME =? WHERE id in(?,?,?,?)";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1,"测试更新2");
        preparedStatement.setInt(2,10101);
        preparedStatement.setInt(3,10001);
        preparedStatement.setInt(4,20001);
        preparedStatement.setInt(5,10100);
        int updateCount = preparedStatement.executeUpdate();
        System.out.println("updateCount = " + updateCount);
    }
    @Test
    public void testSelectInnerJoin() throws SQLException {
//        String sql="SELECT u.id,u.name,ua.account_no,ua.money from user u,user_account ua where u.id=ua.user_id and u.id=?";
        String sql="SELECT u.id,u.name,ua.account_no,ua.money from user u,user_account ua where u.id=ua.user_id and u.id in(?,?,?)";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,10000);
        preparedStatement.setInt(2,10101);
        preparedStatement.setInt(3,10100);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String account_no = resultSet.getString("account_no");
            double money = resultSet.getDouble("money");
            System.out.println("id = " + id+",name="+name+",account_no="+account_no+",money="+money);
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
    }
    @Test
    public void testSelectLeftJoin() throws SQLException {
//        String sql="SELECT u.id,u.name,ua.account_no,ua.money from user u,user_account ua where u.id=ua.user_id and u.id=?";
        String sql="SELECT u.id,u.name,ua.account_no,ua.money FROM user u LEFT JOIN user_account ua ON u.id=ua.user_id WHERE u.id in(?,?,?)";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,10000);
        preparedStatement.setInt(2,10101);
        preparedStatement.setInt(3,10100);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String account_no = resultSet.getString("account_no");
            double money = resultSet.getDouble("money");
            System.out.println("id = " + id+",name="+name+",account_no="+account_no+",money="+money);
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
    }
    @Test
    public void testSelectRightJoin() throws SQLException {
//        String sql="SELECT u.id,u.name,ua.account_no,ua.money from user u,user_account ua where u.id=ua.user_id and u.id=?";
        String sql="SELECT u.id,u.name,ua.account_no,ua.money FROM user u RIGHT JOIN user_account ua ON u.id=ua.user_id WHERE u.id in(?,?,?)";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,10000);
        preparedStatement.setInt(2,10101);
        preparedStatement.setInt(3,10100);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String account_no = resultSet.getString("account_no");
            double money = resultSet.getDouble("money");
            System.out.println("id = " + id+",name="+name+",account_no="+account_no+",money="+money);
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
    }
    @Test
    public void testSelectIn() throws SQLException {
        String sql="SELECT  * FROM user  WHERE id in(?,?,?,?)";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,10101);
        preparedStatement.setInt(2,10001);
        preparedStatement.setInt(3,20001);
        preparedStatement.setInt(4,10100);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            System.out.println("id="+id+",name = " + name);
        }
    }
    @Test
    public void testAlias() throws SQLException {
        String sql="SELECT  u.id,u.name FROM user u  WHERE u.id=?";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,10101);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            System.out.println("id="+id+",name = " + name);
        }
    }
    @Test
    public void testOrderByLimit() throws SQLException {
        String sql="SELECT  * FROM user  WHERE id in(?,?,?,?) order BY id desc limit 2,2";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,10101);
        preparedStatement.setInt(2,10001);
        preparedStatement.setInt(3,20001);
        preparedStatement.setInt(4,10100);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            System.out.println("id="+id+",name = " + name);
        }
    }
    @Test
    public void testAggregateFunciton() throws SQLException {//limit 2,2
        String sql="SELECT  max(id),min(id),sum(id),count(*) FROM user  WHERE id in(?,?,?,?)";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,10101);
        preparedStatement.setInt(2,10001);
        preparedStatement.setInt(3,20001);
        preparedStatement.setInt(4,10100);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            int maxId = resultSet.getInt(1);
            int minId = resultSet.getInt(2);
            BigDecimal sum = resultSet.getBigDecimal(3);
            long count = resultSet.getLong(4);
            System.out.println(" maxId="+maxId+",minId="+minId+",sum = " + sum+",count="+count);
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
    }
    @Test
    public void testAggrGroupBy() throws SQLException {//limit 2,2
        String sql="SELECT  count(*),name FROM user  WHERE id in(?,?,?,?) GROUP by name";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,10101);
        preparedStatement.setInt(2,10001);
        preparedStatement.setInt(3,20001);
        preparedStatement.setInt(4,10100);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            Long count = resultSet.getLong(1);
            String name = resultSet.getString(2);
            System.out.println("name = " + name+",count="+count);
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
    }
    @Test
    public void testUpdateCaseWhen() throws SQLException {
        String sql="UPDATE user" +
                "    SET name = CASE id " +
                "        WHEN 10101 THEN ?" +
                "        WHEN 10001 THEN ?" +
                "        WHEN 20001 THEN ?" +
                "    END " +
                "WHERE id IN (10101,10001,20001)";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        String x = "测试更新case when";
        preparedStatement.setString(1,x);
        preparedStatement.setString(2,x);
        preparedStatement.setString(3,x);
        int updateCount = preparedStatement.executeUpdate();
        System.out.println("updateCount = " + updateCount);
    }


    public static LogicTable makeLogicTable(String tableName, LogicDatabase logicDatabase){
        String namePattern = tableName+"_{0,number,#0000}";
        ArrayList<String> routeRuleStrList = new ArrayList<String>();
        routeRuleStrList.add("${id}.toLong()%10000");
        routeRuleStrList.add("${user_id}.toLong()%10000");
        LogicTable logicTable=new LogicTable(tableName,namePattern, routeRuleStrList,logicDatabase);
        return logicTable;
    }
    private static LogicDatabase makeLogicDataSource() {
        String dbNamePattern="dragon_sharding_{0,number,#00}";
        List<String> dbRouteRules=new ArrayList<String>();
        dbRouteRules.add("${id}.toLong().intdiv(100)%100");
        dbRouteRules.add("${user_id}.toLong().intdiv(100)%100");
        HashMap<String, DataSource> dbIndexDatasourceMap = new HashMap<String, DataSource>();
        dbIndexDatasourceMap.put("dragon_sharding_00",makeDataSource("dragon_sharding_00"));
        dbIndexDatasourceMap.put("dragon_sharding_01",makeDataSource("dragon_sharding_01"));
        dbIndexDatasourceMap.put("dragon_sharding_02",makeDataSource("dragon_sharding_02"));
        dbIndexDatasourceMap.put("dragon_sharding_03",makeDataSource("dragon_sharding_03"));
        dbIndexDatasourceMap.put("dragon_sharding_04",makeDataSource("dragon_sharding_04"));
        dbIndexDatasourceMap.put("dragon_sharding_05",makeDataSource("dragon_sharding_05"));
        dbIndexDatasourceMap.put("dragon_sharding_06",makeDataSource("dragon_sharding_06"));
        dbIndexDatasourceMap.put("dragon_sharding_07",makeDataSource("dragon_sharding_07"));
        dbIndexDatasourceMap.put("dragon_sharding_08",makeDataSource("dragon_sharding_08"));
        dbIndexDatasourceMap.put("dragon_sharding_09",makeDataSource("dragon_sharding_09"));
        return new LogicDatabase(dbNamePattern,dbRouteRules, dbIndexDatasourceMap);
    }

    private static DataSource makeDataSource(String dbName){
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/"+dbName);
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("shxx12151022");
        return druidDataSource;
    }
}