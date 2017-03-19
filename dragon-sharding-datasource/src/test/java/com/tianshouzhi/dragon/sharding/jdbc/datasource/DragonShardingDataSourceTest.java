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
import java.util.*;

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
    public void testSelectAll() throws SQLException {//不指定分区条件，分发到所有表
        String sql="select * from user";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            System.out.println("id="+id+",name = " + name);
        }
    }

    //= != > <  >= <= is is not like not like
    @Test
    public void testSelectBetween() throws SQLException {
        String sql="select * from user where id BETWEEN ? and ?";
//        String sql="select * from user ";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,10000);
        preparedStatement.setInt(2,20000);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            System.out.println("id="+id+",name = " + name);
        }
    }
    @Test
    public void testSelectBinaryCondition() throws SQLException {
        String sql="select * from user where id=10000";
//        String sql="select * from user ";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
//        preparedStatement.setInt(1,10000);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            System.out.println("id="+id+",name = " + name);
        }
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
        String sql="SELECT u.id as user_id,u.name as username,ua.account_no,ua.money from user u,user_account ua where u.id=ua.user_id and u.id in(?,?,?) order by user_id";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,10000);
        preparedStatement.setInt(2,10101);
        preparedStatement.setInt(3,10100);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            int id = resultSet.getInt("user_id");
            String name = resultSet.getString("username");
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
        String sql="SELECT  * FROM user  WHERE id not in(?,?,?,?)";
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
        /*preparedStatement.setInt(5,2);
        preparedStatement.setInt(6,2);*/
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            System.out.println("id="+id+",name = " + name);
        }
    }
    @Test
    public void testAggregateFunciton() throws SQLException {//limit 2,2
        String sql="SELECT  max(id),min(id),sum(id),count(*) FROM user";
//        String sql="SELECT  max(id),min(id),sum(id),count(*) FROM user  WHERE id in(?,?,?,?)";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
       /* preparedStatement.setInt(1,10101);
        preparedStatement.setInt(2,10001);
        preparedStatement.setInt(3,20001);
        preparedStatement.setInt(4,10100);*/
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
    public void testAggrGroupBy() throws SQLException {//验证 tddl语法是否支持
        //fixme 已修复 group by的字段起了别名的情况下，只能按照别别名进行group by 例如以下只能用username，不能用u.name,order by也是一样
        //todo 未修复：使用表名.列名作为select 选项，但是group by 只用列名会出错 例如以下可以用u.name、username，但是不能直接使用name
        String sql="SELECT  count(*) as total_count,u.name as username FROM user u GROUP by u.name";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            Long count = resultSet.getLong("total_count");
            String name = resultSet.getString("username");
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
    @Test
    public void testTransaction() throws SQLException {
        String sql="UPDATE user SET name=? where id=?";
//        String sql="select * from user ";
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,"transaction-success");
            preparedStatement.setInt(2,10000);
            int i = preparedStatement.executeUpdate();
            System.out.println(i);
            PreparedStatement statement = connection.prepareStatement("insert into user(id,name) VALUES (?,?)");
            statement.setInt(1,40000);
            statement.setString(2,"transaction2");
            int i1 = statement.executeUpdate();
            System.out.println(i1);
//            int dd=1/0;
            connection.commit();
        }catch (Exception e){
            e.printStackTrace();
            connection.rollback();
        }

    }

    public static LogicTable makeLogicTable(String tableName, LogicDatabase logicDatabase){
        String namePattern = tableName+"_{0,number,#0000}";
        ArrayList<String> routeRuleStrList = new ArrayList<String>();
        routeRuleStrList.add("${id}.toLong()%10000");
        routeRuleStrList.add("${user_id}.toLong()%10000");
        LogicTable logicTable=new LogicTable(tableName,namePattern, routeRuleStrList,logicDatabase);

        Map<String,List<String>> map=new HashMap<String, List<String>>();
        Set<String> realDBIndexes = logicDatabase.getRealDbIndexDatasourceMap().keySet();
        for (String realDBName : realDBIndexes) {
            Long realDbIndex = logicDatabase.parseIndex(realDBName);
            List<String> realTBNames=new ArrayList<String>();
            for (int i = 0; i < 2; i++) {
                realTBNames.add(logicTable.format(realDbIndex*100+i));
            }
            map.put(realDBName,realTBNames);
        }
        logicTable.setRealDBTBMap(map);
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
/*        dbIndexDatasourceMap.put("dragon_sharding_02",makeDataSource("dragon_sharding_02"));
        dbIndexDatasourceMap.put("dragon_sharding_03",makeDataSource("dragon_sharding_03"));
        dbIndexDatasourceMap.put("dragon_sharding_04",makeDataSource("dragon_sharding_04"));
        dbIndexDatasourceMap.put("dragon_sharding_05",makeDataSource("dragon_sharding_05"));
        dbIndexDatasourceMap.put("dragon_sharding_06",makeDataSource("dragon_sharding_06"));
        dbIndexDatasourceMap.put("dragon_sharding_07",makeDataSource("dragon_sharding_07"));
        dbIndexDatasourceMap.put("dragon_sharding_08",makeDataSource("dragon_sharding_08"));
        dbIndexDatasourceMap.put("dragon_sharding_09",makeDataSource("dragon_sharding_09"));*/
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