package com.tianshouzhi.dragon.common;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Created by TIANSHOUZHI336 on 2016/11/30.
 */
public abstract class DataSourceAdapter extends WrapperAdapter implements DataSource{

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }
}
