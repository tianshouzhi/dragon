package com.tianshouzhi.dragon.sharding.jdbc;

import com.tianshouzhi.dragon.common.jdbc.DragonConnection;
import com.tianshouzhi.dragon.common.jdbc.DragonStatement;

import java.sql.*;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public class DragonShardingStatement extends DragonStatement{

    protected DragonShardingStatement(DragonConnection dragonConnection) {
        super(dragonConnection);
    }

    protected DragonShardingStatement(Integer resultSetType, Integer resultSetConcurrency, Integer resultSetHoldability, DragonConnection dragonConnection) {
        super(resultSetType, resultSetConcurrency, resultSetHoldability, dragonConnection);
    }

    protected DragonShardingStatement(Integer resultSetType, Integer resultSetConcurrency, DragonConnection dragonConnection) {
        super(resultSetType, resultSetConcurrency, dragonConnection);
    }

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
    public void addBatch(String sql) throws SQLException {

    }

    @Override
    public void clearBatch() throws SQLException {

    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }
}
