package com.tianshouzhi.dragon.physical;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Created by tianshouzhi on 2018/1/26.
 */
public class HikariCPTest {
    static long count = 0;
    public static void main(String[] args) throws InterruptedException {
        final HikariDataSource dataSource=new HikariDataSource();
        dataSource.setConnectionTimeout(1000);
        dataSource.setMinimumIdle(25);
        dataSource.setMaximumPoolSize(10);
        dataSource.setUsername("root");
        dataSource.setPassword("shxx12151022");
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        for (int i = 0; i < 50; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        Connection connection = null;
                        try {
                            connection = dataSource.getConnection();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            if (connection != null) {
                                try {
                                    connection.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        count();
                    }

                }
            };
            thread.setDaemon(true);
            thread.start();
        }

        Thread.sleep(60000);
        System.out.println(count);
    }

    public static synchronized void count(){
        count++;
    }
}
