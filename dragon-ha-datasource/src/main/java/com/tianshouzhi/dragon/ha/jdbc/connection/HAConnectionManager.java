package com.tianshouzhi.dragon.ha.jdbc.connection;

import com.tianshouzhi.dragon.ha.dbselector.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.CommonDataSource;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by TIANSHOUZHI336 on 2016/12/3.
 */
public class HAConnectionManager {
    private static final Logger LOGGER= LoggerFactory.getLogger(HAConnectionManager.class);
    private Map<DBIndex, DatasourceWrapper> indexDSMap = new ConcurrentHashMap<DBIndex, DatasourceWrapper>();
    private BlockingQueue<DatasourceWrapper> invalidQueue = new LinkedBlockingDeque<DatasourceWrapper>();
    private DBSelector readDBSelector;
    private DBSelector writeDBSelector;
    private AtomicBoolean isRebuiding =new AtomicBoolean(false);
    public HAConnectionManager(List<DatasourceWrapper> datasourceWrapperList) {
        rebuild(datasourceWrapperList);
        runInvalidRecoveryThread();
    }

    public void rebuild(List<DatasourceWrapper> datasourceWrapperList) {
        if(!isRebuiding.getAndSet(true)){
            LOGGER.info("start building hAConnectionManager...");
            long start=System.currentTimeMillis();
            indexDSMap.clear();
            for (DatasourceWrapper datasourceWrapper : datasourceWrapperList) {
                DBIndex index = datasourceWrapper.getDbIndex();
                if (indexDSMap.containsKey(index)) {
                    throw new RuntimeException("index must be unique,'" + index + "' duplicated");
                }
                indexDSMap.put(index, datasourceWrapper);
            }
            readDBSelector = new ReadDBSelector(datasourceWrapperList);
            writeDBSelector = new WriteDBSelector(datasourceWrapperList);
            isRebuiding.set(false);
            LOGGER.info("end building hAConnectionManager ...elapse:{}ms",System.currentTimeMillis()-start);
        }
    }
    public DBIndex selectWriteDBIndex(){
        while (!isRebuiding.get())break;
        return writeDBSelector.select();
    }
    public DBIndex selectReadDBIndex(){
        while (!isRebuiding.get())break;
        return readDBSelector.select();
    }

    public DatasourceWrapper getDatasourceWrapperByDbIndex(DBIndex dbIndex) throws SQLException{
        while (!isRebuiding.get())break;
        return indexDSMap.get(dbIndex);
    }
    public Connection getConnectionByDbIndex(DBIndex dbIndex,String username,String password) throws SQLException {
        while (!isRebuiding.get())break;
        DatasourceWrapper datasourceWrapper = indexDSMap.get(dbIndex);
        if (datasourceWrapper == null) {
            throw new SQLException("not found datasouce with dbIndex:" + dbIndex);
        }
        LOGGER.debug("get a new connection from {}",datasourceWrapper.getDbIndex().getIndexStr());
        CommonDataSource realDataSource = datasourceWrapper.getRealDataSource();
        Connection connection=null;

        if (realDataSource instanceof XADataSource) {
            connection= ((XADataSource) realDataSource).getXAConnection().getConnection();
        }
        if (connection==null&&realDataSource instanceof ConnectionPoolDataSource) {
            connection= ((ConnectionPoolDataSource) realDataSource).getPooledConnection().getConnection();
        }
        if(connection==null && realDataSource instanceof DataSource){
            if(StringUtils.isAnyBlank(username,password))
            connection = ((DataSource) realDataSource).getConnection(username,password);
            else
                connection = ((DataSource) realDataSource).getConnection();
        }

        if(connection==null){
            throw new SQLException("only support Connection and PooledConnection and XAonnection");
        }
        if(!connection.isReadOnly()&&datasourceWrapper.isReadOnly()&&indexDSMap.get(dbIndex).isReadOnly()){
            connection.setReadOnly(true);
        }
        return connection;

    }

    public Connection getConnectionByDbIndex(List<DBIndex> hintDBIndexes,String username,String password) throws SQLException {
        while (!isRebuiding.get())break;
        if(hintDBIndexes==null&&hintDBIndexes.size()==0){
            throw new SQLException("hintDBIndexes can't be bull or empty");
        }
        if(hintDBIndexes.size()==1){
           return getConnectionByDbIndex(hintDBIndexes.get(0),username,password);
        }
        int randomIndex = new Random().nextInt(hintDBIndexes.size());
        return getConnectionByDbIndex(hintDBIndexes.get(randomIndex),username,password);
    }
    private void runInvalidRecoveryThread() {
        Thread recoveryThread=new Thread("DRAGON-HA-RecoveryThread"){
            @Override
            public void run() {
                try {
                    DatasourceWrapper invalid = invalidQueue.take();
                    while(true){
                        DataSource realDataSource = (DataSource) invalid.getRealDataSource();
                        try {
                            Connection connection = realDataSource.getConnection();
                            if(connection.isValid(3000)){
                                LOGGER.info("datasource '{}' is recovered,try to rebuid.....",invalid.getDbIndex());
                                indexDSMap.put(invalid.getDbIndex(),invalid);
                                rebuild(new ArrayList<DatasourceWrapper>(indexDSMap.values()));
                            }
                        } catch (SQLException e) {
                            //依然失败，将当前失败的加入队列最后一个，这样就能重试下一个失败的数据源，而不是总是重试第一个失败的数据源
                            invalidQueue.add(invalid);
                        }
                    }
                } catch (InterruptedException e) {//不抛出异常，进程无法退出
                    throw new RuntimeException(e);
                }
            }
        };
        recoveryThread.setDaemon(true);
        recoveryThread.start();
    }
    public void invalid(DBIndex dbIndex) {
        while (!isRebuiding.get())break;
        if (indexDSMap.get(dbIndex) != null) {
            DatasourceWrapper datasourceWrapper = indexDSMap.get(dbIndex);
            LOGGER.warn("invalid datasource {}",datasourceWrapper.getDbIndex());
            invalidQueue.add(datasourceWrapper);
            indexDSMap.remove(dbIndex);
            rebuild(new ArrayList<DatasourceWrapper>(indexDSMap.values()));
        }
    }

    public DBIndex selectReadDBIndexExclude(Set<DBIndex> excludes) {
        Set<DBIndex> managedDBIndexes = readDBSelector.getManagedDBIndexes();
        managedDBIndexes.removeAll(excludes);
        if(managedDBIndexes.isEmpty()){
            return null;
        }
        return managedDBIndexes.iterator().next();
    }
}
