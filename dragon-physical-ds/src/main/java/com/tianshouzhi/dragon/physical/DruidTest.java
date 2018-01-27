package com.tianshouzhi.dragon.physical;

import java.sql.SQLException;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

/**
 * Created by tianshouzhi on 2018/1/24.
 */
public class DruidTest {

    static long count = 0;

    public static void main(String[] args) throws InterruptedException, SQLException {
        final DruidDataSource dataSource=new DruidDataSource(false);
        dataSource.setUsername("root");
        dataSource.setPassword("shxx12151022");
        dataSource.setUrl("jdbc:mysql://localhost:3306/test");
        dataSource.setInitialSize(0);
        dataSource.setMaxActive(10);
        dataSource.setMinIdle(5);
        dataSource.setMaxWait(1);

        DruidPooledConnection connection = dataSource.getConnection();

//        dataSource.init();
//        for (int i = 0; i < 50; i++) {
//            Thread thread = new Thread() {
//                @Override
//                public void run() {
//                    while (true) {
//                        Connection connection = null;
//                        try {
//                            connection = dataSource.borrowConnection();
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
//
//        Thread.sleep(60000);
//        System.out.println(count);
    }

    public static synchronized void count(){
        count++;
    }
}
