package com.tianshouzhi.dragon.single;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by tianshouzhi on 2018/1/26.
 */
public class HikariCPTest {
    static long count = 0;
    public static void main(String[] args) throws InterruptedException, SQLException {
        final HikariDataSource dataSource=new HikariDataSource();
//        dataSource.setConnectionTimeout(1000);
        dataSource.setMinimumIdle(1);
        dataSource.setMaximumPoolSize(1);
        dataSource.setUsername("root");
        dataSource.setPassword("shxx12151022");
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test?socketTimeout=1");
        Connection connection=null;
        try{
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT sleep(2) ");
            preparedStatement.executeQuery();
        }catch (Exception e){
//            e.printStackTrace();
            System.out.println(connection.isClosed());
            connection.close();
        }
        connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("select * from USER ;");
        preparedStatement.executeQuery();

//        for (int i = 0; i < 50; i++) {
//            Thread thread = new Thread() {
//                @Override
//                public void run() {
//                    while (true) {
//                        Connection connection = null;
//                        try {
//                            connection = dataSource.getConnection();
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        } finally {
//                            if (connection != null) {
//                                try {
//                                    connection.close();
//                                } catch (SQLException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                        count();
//                    }
//
//                }
//            };
//            thread.setDaemon(true);
//            thread.start();
//        }

        Thread.sleep(60000);
        System.out.println(count);
    }

    public static synchronized void count(){
        count++;
    }
}
