package com.tianshouzhi.dragon.physical;

import java.sql.*;

/**
 * Created by tianshouzhi on 2018/1/23.
 */
public class MyDataSource extends DataSourceAdapter {

    private boolean init = false;

    private DefaultConnectionPool connectionPool;

    public synchronized void init() {
        try {
            if (init) {
                return;
            }
            checkConfig();
            initPool();
            init = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void checkConfig() {
        if (StringUtil.isAnyBlank(username, url, driverClassName)) {
            throw new RuntimeException("username，url，driverClassName can't empty or blank!!!");
        }
        if (initPoolSize < 0 || minPoolSize < 0 || maxPoolSize < minPoolSize) {
            throw new RuntimeException("initPoolSize，minPoolSize must greater than 0, and maxPoolSize must greater or equals to minPoolSize");
        }

        if (checkConnectionTimeout < 0) {
            throw new RuntimeException("checkConnectionTimeout must greater or equals to 0");
        }
    }

    private void initPool() throws Exception {
        DriverManager.registerDriver((Driver) Class.forName(driverClassName).newInstance());
        this.connectionPool = new DefaultConnectionPool(url, username,passowrd, initPoolSize, minPoolSize, maxPoolSize, checkConnectionTimeout);
    }


    @Override
    public Connection getConnection() throws SQLException {
        init();
        try {
            return connectionPool.borrowConnection();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new SQLFeatureNotSupportedException("borrowConnection(String username, String password) is not Support!!!");
    }

}
