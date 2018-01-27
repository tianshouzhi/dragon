package com.tianshouzhi.dragon.physical;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by tianshouzhi on 2018/1/24.
 */
public class MyDataSourceTest {
    static long count = 0;

    public static void main(String[] args) throws InterruptedException {
        final MyDataSource myDataSource = new MyDataSource();
        myDataSource.setUsername("root");
        myDataSource.setPassowrd("shxx12151022");
        myDataSource.setUrl("jdbc:mysql://localhost:3306/test");
        myDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        myDataSource.setInitPoolSize(5);
        myDataSource.setMaxPoolSize(10);
        myDataSource.setMinPoolSize(5);
        myDataSource.init();
        myDataSource.setCheckConnectionTimeout(1000);
        for (int i = 0; i < 50; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        Connection connection = null;
                        try {
                            connection = myDataSource.getConnection();
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
