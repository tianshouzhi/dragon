package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.jdbc.datasource.DataSourceIndex;
import com.tianshouzhi.dragon.ha.jdbc.datasource.dbselector.DBSelector;
import com.tianshouzhi.dragon.ha.jdbc.datasource.dbselector.DatasourceWrapper;
import com.tianshouzhi.dragon.ha.jdbc.datasource.dbselector.ReadDBSelector;
import com.tianshouzhi.dragon.ha.jdbc.datasource.dbselector.WriteDBSelector;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
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
public class HADataSourceManager{
    private static final Logger LOGGER= LoggerFactory.getLogger(HADataSourceManager.class);
    private Map<DataSourceIndex, DatasourceWrapper> indexDSMap = new ConcurrentHashMap<DataSourceIndex, DatasourceWrapper>();
    private BlockingQueue<DatasourceWrapper> invalidQueue = new LinkedBlockingDeque<DatasourceWrapper>();
    private DBSelector readDBSelector;
    private DBSelector writeDBSelector;
    private AtomicBoolean isRebuiding =new AtomicBoolean(false);
    public HADataSourceManager(List<DatasourceWrapper> datasourceWrapperList) {
        rebuild(datasourceWrapperList);
        runInvalidRecoveryThread();
    }

    public void rebuild(List<DatasourceWrapper> datasourceWrapperList) {
        if(isRebuiding.compareAndSet(false,true)){
            LOGGER.info("start building hADataSourceManager...");
            long start=System.currentTimeMillis();
            indexDSMap.clear();
            for (DatasourceWrapper datasourceWrapper : datasourceWrapperList) {
                DataSourceIndex index = datasourceWrapper.getDataSourceIndex();
                if (indexDSMap.containsKey(index)) {
                    throw new RuntimeException("dataSourceIndex must be unique,'" + index + "' duplicated");
                }
                indexDSMap.put(index, datasourceWrapper);
            }
            readDBSelector = new ReadDBSelector(datasourceWrapperList);
            writeDBSelector = new WriteDBSelector(datasourceWrapperList);
            isRebuiding.set(false);
            LOGGER.info("end building hADataSourceManager ...elapse:{}ms",System.currentTimeMillis()-start);
        }
    }
    public DataSourceIndex selectWriteDBIndex(){
        while (!isRebuiding.get())break;
        return writeDBSelector.select();
    }
    public DataSourceIndex selectReadDBIndex(){
        while (!isRebuiding.get())break;
        return readDBSelector.select();
    }

    public DatasourceWrapper getDatasourceWrapperByDbIndex(DataSourceIndex dataSourceIndex) throws SQLException{
        while (!isRebuiding.get())break;
        return indexDSMap.get(dataSourceIndex);
    }
    public Connection getConnectionByDbIndex(DataSourceIndex dataSourceIndex, String username, String password) throws SQLException {
        while (!isRebuiding.get())break;
        DatasourceWrapper datasourceWrapper = indexDSMap.get(dataSourceIndex);
        if (datasourceWrapper == null) {
            throw new IllegalArgumentException("not found datasouce with dataSourceIndex:" + dataSourceIndex.getIndexStr());
        }
        DataSource realDataSource = datasourceWrapper.getPhysicalDataSource();
        Connection connection=null;

        if(StringUtils.isAnyBlank(username,password))
            connection = realDataSource.getConnection();
        else
            connection = realDataSource.getConnection(username,password);//druid不支持这个方法
        if(!connection.isReadOnly()&&datasourceWrapper.isReadOnly()&&indexDSMap.get(dataSourceIndex).isReadOnly()){
            connection.setReadOnly(true);
        }
        return connection;
    }

    public Connection getConnectionByDbIndex(List<DataSourceIndex> hintDataSourceIndices, String username, String password) throws SQLException {
        while (!isRebuiding.get())break;
        if(hintDataSourceIndices ==null&& hintDataSourceIndices.size()==0){
            throw new SQLException("hintDataSourceIndices can't be bull or empty");
        }
        if(hintDataSourceIndices.size()==1){
           return getConnectionByDbIndex(hintDataSourceIndices.get(0),username,password);
        }
        int randomIndex = new Random().nextInt(hintDataSourceIndices.size());
        return getConnectionByDbIndex(hintDataSourceIndices.get(randomIndex),username,password);
    }
    private void runInvalidRecoveryThread() {
        Thread recoveryThread=new Thread("DRAGON-HA-RecoveryThread"){
            @Override
            public void run() {
                try {
                    DatasourceWrapper invalid = invalidQueue.take();
                    while(true){
                        DataSource realDataSource = (DataSource) invalid.getPhysicalDataSource();
                        try {
                            Connection connection = realDataSource.getConnection();
                            if(connection.isValid(3000)){
                                LOGGER.info("datasource '{}' is recovered,try to rebuid.....",invalid.getDataSourceIndex());
                                indexDSMap.put(invalid.getDataSourceIndex(),invalid);
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
    public void invalid(DataSourceIndex dataSourceIndex) {
        while (!isRebuiding.get())break;
        if (indexDSMap.get(dataSourceIndex) != null) {
            DatasourceWrapper datasourceWrapper = indexDSMap.get(dataSourceIndex);
            LOGGER.warn("invalid datasource {}",datasourceWrapper.getDataSourceIndex());
            invalidQueue.add(datasourceWrapper);
            indexDSMap.remove(dataSourceIndex);
            rebuild(new ArrayList<DatasourceWrapper>(indexDSMap.values()));
        }
    }

    public DataSourceIndex selectReadDBIndexExclude(Set<DataSourceIndex> excludes) {
        Set<DataSourceIndex> managedDataSourceIndices = readDBSelector.getManagedDBIndexes();
        managedDataSourceIndices.removeAll(excludes);
        if(managedDataSourceIndices.isEmpty()){
            return null;
        }
        return managedDataSourceIndices.iterator().next();
    }
}
