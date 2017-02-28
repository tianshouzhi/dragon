package com.tianshouzhi.dragon.dynamic.jdbc;

import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 动态数据源只需要实现一个DataSource接口即可，Connection、Statement等接口不用实现
 */
public class DragonDynamicDataSource extends DragonDataSource {

    private DataSource realDataSource;
    /**
     * 远程配置地址
     */
    private String remoteConfAddr;
    /**
     * 远程配置key
     */
    private String key;

    public DragonDynamicDataSource(String remoteConfAddr, String key) {
        this.remoteConfAddr = remoteConfAddr;
        this.key = key;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return realDataSource.getConnection(username,password);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return realDataSource.getLoginTimeout();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
         realDataSource.setLoginTimeout(seconds);
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        realDataSource.setLogWriter(out);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return realDataSource.getLogWriter();
    }
}
