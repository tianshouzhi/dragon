package com.tianshouzhi.dragon.sharding.jdbc.statement;

import com.tianshouzhi.dragon.common.jdbc.statement.DragonStatement;
import com.tianshouzhi.dragon.sharding.jdbc.connection.DragonShardingConnection;

import java.sql.SQLException;
import java.sql.SQLWarning;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public class DragonShardingStatement extends DragonStatement{
    private DragonShardingConnection dragonShardingConnection;
    public DragonShardingStatement(DragonShardingConnection dragonShardingConnection) {
        this.dragonShardingConnection = dragonShardingConnection;
    }
    public DragonShardingStatement(int resultSetType, int resultSetConcurrency, DragonShardingConnection dragonShardingConnection) {
        super(resultSetType,resultSetConcurrency);
        this.dragonShardingConnection = dragonShardingConnection;
    }
    public DragonShardingStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability, DragonShardingConnection dragonShardingConnection) {
        super(resultSetType,resultSetConcurrency,resultSetHoldability);
        this.dragonShardingConnection = dragonShardingConnection;
    }

    @Override
    protected boolean doExecute() throws SQLException {
        return false;
    }

    @Override
    public void close() throws SQLException {
        if(resultSet!=null){
            resultSet.close();
        }
    }

    @Override
    public void cancel() throws SQLException {
        checkClosed();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        checkClosed();
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        checkClosed();
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        checkClosed();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        checkClosed();
        return false;
    }

    @Override
    public void clearBatch() throws SQLException {
        checkClosed();
    }

    @Override
    public DragonShardingConnection getConnection() throws SQLException {
        checkClosed();
        return dragonShardingConnection;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        checkClosed();
        return false;
    }
}
