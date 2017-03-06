package com.tianshouzhi.dragon.sharding.jdbc.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.tianshouzhi.dragon.sharding.route.LogicDatabase;
import com.tianshouzhi.dragon.sharding.route.LogicTable;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
        logicTableMap.put("user",logicTable);
        dataSource=new DragonShardingDataSource(logicDatabase, logicTableMap);
    }

    @Test
    public void testInsert() throws SQLException {
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
    public void testDelete() throws SQLException {
        String sql="delete from user where id in(?,?,?,?,?,?,?,?)";
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

    public static LogicTable makeLogicTable(String tableName, LogicDatabase logicDatabase){
        String namePattern = tableName+"_{0,number,#0000}";
        ArrayList<String> routeRuleStrList = new ArrayList<String>();
        routeRuleStrList.add("${id}.toLong()%10000");
        LogicTable logicTable=new LogicTable(namePattern, routeRuleStrList,logicDatabase);
        return logicTable;
    }
    private static LogicDatabase makeLogicDataSource() {
        String dbNamePattern="dragon_sharding_{0,number,#00}";
        List<String> dbRouteRules=new ArrayList<String>();
        dbRouteRules.add("${id}.toLong().intdiv(100)%100");
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