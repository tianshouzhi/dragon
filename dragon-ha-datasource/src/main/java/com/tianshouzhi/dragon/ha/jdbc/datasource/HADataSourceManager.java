package com.tianshouzhi.dragon.ha.jdbc.datasource;

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
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by TIANSHOUZHI336 on 2016/12/3.
 */
public class HADataSourceManager{
    private static final Logger LOGGER= LoggerFactory.getLogger(HADataSourceManager.class);
    private Map<String, DatasourceWrapper> indexDSMap = new ConcurrentHashMap<String, DatasourceWrapper>();
    private Map<String,DatasourceWrapper> invalidDsMap = new ConcurrentHashMap<String,DatasourceWrapper>();
    private DBSelector readDBSelector;
    private DBSelector writeDBSelector;
    private boolean isRebuiding = false;
    private Lock rebuildLock=new ReentrantLock();

    public HADataSourceManager() {
        runInvalidRecoveryThread();
    }

    public void rebuild() {
        try {
            rebuildLock.lockInterruptibly();
            isRebuiding=true;
            LOGGER.info("start building hADataSourceManager...");
            long start=System.currentTimeMillis();
            readDBSelector = new ReadDBSelector(indexDSMap);
            writeDBSelector = new WriteDBSelector(indexDSMap);
            LOGGER.info("end building hADataSourceManager ...elapse:{}ms",System.currentTimeMillis()-start);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            isRebuiding=false;
            rebuildLock.unlock();
        }
    }
    public String selectWriteDBIndex(){
        while (!isRebuiding)break;
        return writeDBSelector.select();
    }
    public String selectReadDBIndex(){
        while (!isRebuiding)break;
        return readDBSelector.select();
    }

    public DatasourceWrapper getDatasourceWrapperByDbIndex(String dataSourceIndex) throws SQLException{
        while (!isRebuiding)break;
        return indexDSMap.get(dataSourceIndex);
    }
    public Connection getConnectionByDbIndex(String dataSourceIndex, String username, String password) throws SQLException {
        while (!isRebuiding)break;
        DatasourceWrapper datasourceWrapper = indexDSMap.get(dataSourceIndex);
        if (datasourceWrapper == null) {
            throw new IllegalArgumentException("not found datasouce with dataSourceIndex:" + dataSourceIndex);
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

    public Connection getConnectionByDbIndex(List<String> hintDataSourceIndices, String username, String password) throws SQLException {
        while (!isRebuiding)break;
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
        Thread recoveryThread = new Thread("DRAGON-HA-RecoveryThread") {
            @Override
            public void run() {

                while (true) {
                    if (!invalidDsMap.isEmpty()) {
                        for (Map.Entry<String, DatasourceWrapper> entry : invalidDsMap.entrySet()) {
                            String dsIndex = entry.getKey();
                            DatasourceWrapper datasourceWrapper = entry.getValue();
                            DataSource realDataSource = (DataSource) datasourceWrapper.getPhysicalDataSource();
                            try {
                                Connection connection = realDataSource.getConnection();
                                if (connection.isValid(3000)) {
                                    LOGGER.info("datasource '{}' is recovered,try to rebuid.....", dsIndex);
                                    invalidDsMap.remove(dsIndex);
                                    indexDSMap.put(dsIndex, datasourceWrapper);
                                    rebuild();
                                }
                            } catch (SQLException e) {
                                //依然失败，将当前失败的加入队列最后一个，这样就能重试下一个失败的数据源，而不是总是重试第一个失败的数据源
                                LOGGER.error("try recover {} error", dsIndex,e);
                            }
                        }
                    }
                }
            }
        };
        recoveryThread.setDaemon(true);
        recoveryThread.start();
    }
    public void invalid(String dataSourceIndex) {
        while (!isRebuiding)break;
        if (indexDSMap.get(dataSourceIndex) != null) {
            DatasourceWrapper datasourceWrapper = indexDSMap.get(dataSourceIndex);
            LOGGER.warn("invalid datasource {}",dataSourceIndex);
            invalidDsMap.put(dataSourceIndex,datasourceWrapper);
            indexDSMap.remove(dataSourceIndex);
            rebuild();
        }
    }

    public String selectReadDBIndexExclude(Set<String> excludes) {
        Set<String> managedDataSourceIndices = readDBSelector.getManagedDBIndexes();
        managedDataSourceIndices.removeAll(excludes);
        if(managedDataSourceIndices.isEmpty()){
            return null;
        }
        return managedDataSourceIndices.iterator().next();
    }

    public Map<String, DatasourceWrapper> getIndexDSMap() {
        return indexDSMap;
    }

    void setIndexDSMap(Map<String, DatasourceWrapper> indexDSMap) {
        this.indexDSMap = indexDSMap;
    }
}
