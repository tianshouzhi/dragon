package com.tianshouzhi.dragon.ha.jdbc.connection;

import com.tianshouzhi.dragon.common.WrapperAdapter;
import com.tianshouzhi.dragon.ha.dbselector.DBIndex;
import com.tianshouzhi.dragon.ha.hint.SqlHintUtil;
import com.tianshouzhi.dragon.ha.hint.ThreadLocalHintUtil;
import com.tianshouzhi.dragon.ha.sqltype.SqlTypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * Created by TIANSHOUZHI336 on 2016/12/3.
 */
public abstract class HAConnectionAdapter extends WrapperAdapter implements Connection {
    private static final Logger LOGGER= LoggerFactory.getLogger(HAConnectionAdapter.class);
    /**
     * 在没有使用读写分离的情况下，用户可能在一个Connection即执行读，也执行写。
     * 在使用读写分离的时候，读需要使用读连接(ReadDBSelector)，写需要使用写连接(WriteDBSelector)。
     * 为了简化使用，实现者只需要通过调用getRealConnection方法，既可以获取对应的连接
     * <p>
     * 在事务的情况下，总是需要保持对同一个连接的引用
     * 对于不是事务的情况下，则可以每次重新通过DBSelector进行选择
     */
    protected Connection realConnection;
    protected HAConnectionManager hAConnectionManager;
    private String userName;
    private String password;

    private DBIndex dbIndex;//当前连接是从哪一个数据源中获取的

    public HAConnectionAdapter(HAConnectionManager hAConnectionManager) throws SQLException {
        this(null,null, hAConnectionManager);
    }

    public HAConnectionAdapter(String userName, String password, HAConnectionManager hAConnectionManager) throws SQLException {
        this.userName = userName;
        this.password = password;
        this.hAConnectionManager = hAConnectionManager;
        if(hAConnectionManager ==null){
            throw new SQLException("parameter 'hAConnectionManager' can't be null");
        }
    }

    /**
     * 获取真实连接，会根据以下情况自动切换真实连接
     * 1 当前是只读连接，但是开启了事务
     * 2 当前是只读连接，但是传入了一个写sql
     * 3 其他情况，返回当前连接
     * @param sql
     * @return
     * @throws SQLException
     */
    public Connection getRealConnection(String sql,boolean useSqlTypeCache) throws SQLException {

        //如果已经开启了事务
        if (autoCommit == false) {
            if (realConnection == null) {
                LOGGER.debug("autoCommit=false and current real connection is null,get a new write connection,sql:{}",sql);
                buildNewWriteConnection();
                return realConnection;
            }

            if (realConnection != null && realConnection.isReadOnly()) {
                LOGGER.debug("autoCommit=false and current real connection is read only ,get a new write connection,sql:{}",sql);
                buildNewWriteConnection();
                return realConnection;
            }

            if (realConnection != null && realConnection.isReadOnly() == false) {
                LOGGER.debug("autoCommit is false and current real connection is a write connection,return current,sql:{}",sql);
                return realConnection;
            }
        }

        //如果没有开启事务

        //1、判断有没有ThreadLocal hint
        List<DBIndex> hintDBIndexes = ThreadLocalHintUtil.getHintDBIndexes();
        if(hintDBIndexes!=null&&hintDBIndexes.size()>0){
            LOGGER.debug("get connection by thread local hint,sql:{}",sql);
            buildNewConnectionByHint(hintDBIndexes);
            return realConnection;
        }

        //2、sql中有hint
        hintDBIndexes = SqlHintUtil.getSQLHintDBIndex(sql);
        if(hintDBIndexes!=null){
            LOGGER.debug("get connection by sql hint,sql:{}",sql);
            buildNewConnectionByHint(hintDBIndexes);
            return realConnection;
        }
        //3、没有hint且没有开启事务
        boolean sqlIsQuery = SqlTypeUtil.isQuery(sql,useSqlTypeCache);
        if (realConnection == null) {
            if (sqlIsQuery) {
                LOGGER.debug("current real connection is null,sql:({}) is query,get a new read connection",sql);
                buildNewReadConnection();
            } else {
                LOGGER.debug("current real connection is null,sql:({}) is write,get a new write connection",sql);
                buildNewWriteConnection();
            }
            return realConnection;
        } else {
            if (( //1 如果当前connection是只读的(只读数据源中获取的所有connection都会设置isReadOnly=true)，并且如果sql是查询的话，直接返回当前connection
                    realConnection.isReadOnly() && sqlIsQuery)
                    //2 如果当前connection是可写的(一般也可读)，分两种情况：
                    // 2.1 (!realConnection.isReadOnly() && !sqlIsQuery) sql不是query，直接返回当前connection
                    // 2.2 (!realConnection.isReadOnly() && sqlIsQuery) 当前connection是可写的，但是却要获取一个读connection
                    // 返回当前connection不会存在因为延迟导致从从库中查询不到数据的情况，例如插入完 立马进行查询新插入的数据
                    //这样对于一些强一致性的场景，用户不需要添加任何hint，简化了用户的使用
                    //todo 问题在于如果查询的不是插入的记录，而是其他内容，这个时候应该从从库查询 ，此时依然用写connection进行查询，会增大写库的压力，考虑到一般都是读多写少的情况，因此压力也不会增大太多
                    || (!realConnection.isReadOnly())) {
                if(!realConnection.isReadOnly()){
                    LOGGER.debug("current real connection is a write connection,return current,sql:{}",sql);
                }else{
                    LOGGER.debug("current real connection is read only,and sql:({}) is query,return current connection",sql);
                }
                return realConnection;
            } else {
                //1、如果当前的connection是只读的，但是却要获取一个写connection，关闭当前connection，获取一个新的写connection
                LOGGER.debug("current real connection is read only,and sql:({}) is write,return a new write connection",sql);
                if(sqlIsQuery){
                    buildNewReadConnection();
                }else{
                    buildNewWriteConnection();
                }
                return realConnection;
            }
        }
    }

    private void buildNewConnectionByHint(List<DBIndex> dbIndexes) throws SQLException {
        if(dbIndexes.contains(dbIndex)){
            LOGGER.debug("current connection's dbIndex is {}, return current",dbIndex);
            return;
        }
        if(realConnection!=null){
            realConnection.close();
        }
        int i = new Random().nextInt(dbIndexes.size());
        dbIndex = dbIndexes.get(i);
        realConnection = hAConnectionManager.getConnectionByDbIndex(dbIndex,userName,password);
        setRealConnectionParams();
    }
    private void buildNewReadConnection() throws SQLException {
        if(realConnection!=null){
            realConnection.close();
        }
        dbIndex = hAConnectionManager.selectReadDBIndex();
        realConnection = hAConnectionManager.getConnectionByDbIndex(dbIndex,userName,password);
        setRealConnectionParams();
    }
    public Connection buildNewReadConnectionExclue(Set<DBIndex> excludes) throws SQLException {
        if(realConnection!=null){
            realConnection.close();
        }
        DBIndex dbIndex=hAConnectionManager.selectReadDBIndexExclude(excludes);
        if(dbIndex==null){
            return null;
        }else{
            this.dbIndex=dbIndex;
            realConnection=hAConnectionManager.getConnectionByDbIndex(dbIndex,userName,password);
            return realConnection;
        }
    }
    public Connection buildNewWriteConnection() throws SQLException {
        if(realConnection!=null){
            realConnection.close();
        }
        dbIndex = hAConnectionManager.selectWriteDBIndex();
        realConnection = hAConnectionManager.getConnectionByDbIndex(dbIndex,userName,password);
        setRealConnectionParams();
        return realConnection;
    }

    public HAConnectionManager getHAConnectionManager() {
        return hAConnectionManager;
    }

    protected boolean isClosed = false;

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return null;
    }

    @Override
    public void close() throws SQLException {
        if (realConnection != null) {
            realConnection.close();
            realConnection=null;
        }
        isClosed = true;
    }


    @Override
    public boolean isClosed() throws SQLException {
        return isClosed;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
//        checkClosed();
        if (realConnection != null) {
            realConnection.getMetaData();
        }
        return null;
    }

    //=============================事务相关==============================
    private boolean autoCommit = true;

    private Integer level;//如果为null，则不设置真实Connection的事务隔离级别

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.autoCommit = autoCommit;
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return autoCommit;
    }

    @Override
    public void commit() throws SQLException {
//        checkClosed();
        if (realConnection != null) {
            realConnection.commit();
        }
    }

    /**
     * 回滚到事务的最开始
     */
    @Override
    public void rollback() throws SQLException {
//        checkClosed();
        if(realConnection!=null){
            realConnection.rollback();
        }
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
//        checkClosed();
        if (realConnection != null) {
            realConnection.rollback(savepoint);
        }
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
//        checkClosed();
        Savepoint savepoint=null;
        if(realConnection!=null){
           return realConnection.setSavepoint();
        }
        return savepoint;
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
//        checkClosed();
        Savepoint savepoint=null;
        if(realConnection!=null){
            return realConnection.setSavepoint(name);
        }
        return savepoint;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        this.level = level;
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        if(realConnection!=null){
            return realConnection.getTransactionIsolation();
        }
        return level;
    }

    /**
     * JDBC规范，移除一个savepoint的时候，需要将这个savepoint以及之后的savepoint都移除，
     * 当调用已经移除的savepoint的方法时，抛出SQLException异常
     *
     * @param savepoint
     * @throws SQLException
     */
    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        if(realConnection!=null)
            realConnection.releaseSavepoint(savepoint);
    }

    protected Boolean isReadOnly = false;

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        if (autoCommit) {
            throw new SQLException("This method cannot be called during a transaction");
        } else {
            this.isReadOnly = readOnly;
        }
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        if(realConnection!=null){
            return realConnection.isReadOnly();
        }
        return isReadOnly;
    }

    //====================================================================
    private String catalog;

    @Override
    public void setCatalog(String catalog) throws SQLException {
        this.catalog = catalog;
    }

    @Override
    public String getCatalog() throws SQLException {
        if(realConnection!=null){
            return realConnection.getCatalog();
        }
        return catalog;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        if (realConnection != null) {
            return realConnection.getWarnings();
        }
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
//        checkClosed();
        if (realConnection != null) {
            realConnection.clearWarnings();
        }
    }
    private Map<String, Class<?>> typeMap;
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        if(realConnection!=null){
            return realConnection.getTypeMap();
        }
        return typeMap;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        typeMap=map;
    }

    private Integer holdability;

    @Override
    public void setHoldability(int holdability) throws SQLException {
        this.holdability = holdability;
    }

    @Override
    public int getHoldability() throws SQLException {
        if(realConnection!=null){
            return realConnection.getHoldability();
        }
        return holdability;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return false;
    }
    protected Properties clientInfo=new Properties();
    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        this.clientInfo.put(name,value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        clientInfo=properties;
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return clientInfo.getProperty(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        if(realConnection!=null){
            return realConnection.getClientInfo();
        }
        return clientInfo;
    }
   //======================调用create方法，默认创建一个写连接，因为只有插入或更新的的时候，可能才会用到create方法=========
    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        changeToWtiteConnectionIfNeed();
        return realConnection.createArrayOf(typeName,elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        changeToWtiteConnectionIfNeed();
        return realConnection.createStruct(typeName,attributes);
    }
    @Override
    public Clob createClob() throws SQLException {
        changeToWtiteConnectionIfNeed();
        return realConnection.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        changeToWtiteConnectionIfNeed();
        return realConnection.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        changeToWtiteConnectionIfNeed();
        return realConnection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        changeToWtiteConnectionIfNeed();
        return realConnection.createSQLXML();
    }

    public Connection changeToWtiteConnectionIfNeed() throws SQLException {
        if(realConnection==null){
            buildNewWriteConnection();
        }else{
            if(realConnection.isReadOnly()){
                realConnection.close();
                buildNewWriteConnection();
            }
        }
        return realConnection;
    }

    protected void setRealConnectionParams() throws SQLException {
        realConnection.setAutoCommit(autoCommit);
        if(clientInfo.size()!=0){
            realConnection.setClientInfo(clientInfo);
        }
        if(holdability!=null){
            realConnection.setHoldability(holdability);
        }
        if(typeMap!=null){
            realConnection.setTypeMap(typeMap);
        }
        if(catalog!=null){
            realConnection.setCatalog(catalog);
        }
        if(isReadOnly!=null){
            //因为ReadDBSelector会将所有的只读连接设置为readonly，如果这里设置了，则会对判断造成影响
//            if (!realConnection.isReadOnly())
//            realConnection.setReadOnly(isReadOnly());
        }
        if(level!=null){
            realConnection.setTransactionIsolation(level);
        }
    }

    public DBIndex getDBIndex() {
        return dbIndex;
    }

    public Connection getCurrentRealConnection() {
        return realConnection;
    }

  /*  protected void checkClosed() throws SQLException {
        if (isClosed) {
            throw new SQLException("No operations allowed after connection closed.");
        }
    }*/
}
