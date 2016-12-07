package com.tianshouzhi.dragon.ha.jdbc.connection;

import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import javax.sql.StatementEventListener;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by TIANSHOUZHI336 on 2016/12/3.
 */
public class DragonDynamicPooledConnection  implements PooledConnection {

    private DragonHAConnection dragonHAConnection;

    protected Set<ConnectionEventListener> connectionEventListenerAddSet = Collections.synchronizedSet(new HashSet<ConnectionEventListener>());
    protected Set<ConnectionEventListener> connectionEventListenerRemoveSet = Collections.synchronizedSet(new HashSet<ConnectionEventListener>());
    protected Set<StatementEventListener> statementEventListenerAddSet = Collections.synchronizedSet(new HashSet<StatementEventListener>());
    protected Set<StatementEventListener> statementEventListenerRemoveSet = Collections.synchronizedSet(new HashSet<StatementEventListener>());

    public DragonDynamicPooledConnection(HAConnectionManager HAConnectionManager) throws SQLException {
        this(null,null, HAConnectionManager);
    }

    public DragonDynamicPooledConnection(String username, String password, HAConnectionManager HAConnectionManager) throws SQLException {
        this.dragonHAConnection =new DragonHAConnection(username,password, HAConnectionManager);
    }

    @Override
    public DragonHAConnection getConnection() throws SQLException {
        return dragonHAConnection;
    }

    @Override
    public void close() throws SQLException {
        if (dragonHAConnection != null) {
            dragonHAConnection.close();
        }
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        connectionEventListenerAddSet.add(listener);
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        connectionEventListenerRemoveSet.add(listener);
    }

    @Override
    public void addStatementEventListener(StatementEventListener listener) {
        statementEventListenerAddSet.add(listener);
    }

    @Override
    public void removeStatementEventListener(StatementEventListener listener) {
        statementEventListenerRemoveSet.add(listener);
    }
}
