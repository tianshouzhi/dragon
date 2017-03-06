package com.tianshouzhi.dragon.common.jdbc.datasource;

import com.tianshouzhi.dragon.common.jdbc.WrapperAdapter;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by TIANSHOUZHI336 on 2016/11/30.
 */
public abstract class DragonDataSource extends WrapperAdapter implements DataSource{
    protected AtomicBoolean inited=new AtomicBoolean(false);
    protected int loginTimeout=0;
    private PrintWriter logWriter;
    public void init(){
        if(inited.compareAndSet(false,true)){
            doInit();
        }
    }

    protected  void doInit(){};

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        logWriter=out;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        loginTimeout=seconds;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return loginTimeout;
    }

    @Override
    public Connection getConnection() throws SQLException {
        init();
        return getConnection(null,null);
    }
}
