package com.tianshouzhi.dragon.dynamic.jdbc;

import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 只需要实现一个DataSource接口即可，Connection、Statement等接口不用实现
 */
public class DragonDynamicDataSource extends DragonDataSource {

    private Class<DataSource> dataSourceClass;
    private DataSource wrapperedDataSource;
    /**
     * 远程配置地址
     */
    private String remoteConfAddr;
    /**
     * 远程配置key
     */
    private String remoteKey;

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return wrapperedDataSource.getConnection(username,password);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return wrapperedDataSource.getLoginTimeout();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
         wrapperedDataSource.setLoginTimeout(seconds);
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        wrapperedDataSource.setLogWriter(out);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return wrapperedDataSource.getLogWriter();
    }

    public DataSource getWrapperedDataSource() {
        return wrapperedDataSource;
    }

    public void setWrapperedDataSource(DataSource wrapperedDataSource) {
        this.wrapperedDataSource = wrapperedDataSource;
    }

    public String getRemoteConfAddr() {
        return remoteConfAddr;
    }

    public void setRemoteConfAddr(String remoteConfAddr) {
        this.remoteConfAddr = remoteConfAddr;
    }

    public String getRemoteKey() {
        return remoteKey;
    }

    public void setRemoteKey(String remoteKey) {
        this.remoteKey = remoteKey;
    }

    public Class<DataSource> getDataSourceClass() {
        return dataSourceClass;
    }

    public void setDataSourceClass(Class<DataSource> dataSourceClass) {
        this.dataSourceClass = dataSourceClass;
    }
}
