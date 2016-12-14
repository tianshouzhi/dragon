package com.tianshouzhi.dragon.sharding.jdbc;

import com.tianshouzhi.dragon.common.jdbc.DragonStatement;

import java.sql.SQLException;
import java.sql.SQLWarning;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public class DragonShardingStatement extends DragonStatement{

    @Override
    protected boolean doExecute() throws SQLException {
        return false;
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public void cancel() throws SQLException {

    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public void setCursorName(String name) throws SQLException {

    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return false;
    }

    @Override
    public void clearBatch() throws SQLException {

    }

    @Override
    public DragonShardingConnection getConnection() throws SQLException {
        return null;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }
}
