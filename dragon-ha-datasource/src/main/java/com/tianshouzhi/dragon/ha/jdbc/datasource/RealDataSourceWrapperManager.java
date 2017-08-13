package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.log.Log;
import com.tianshouzhi.dragon.common.log.LoggerFactory;
import com.tianshouzhi.dragon.ha.exception.DragonHARuntimeException;
import com.tianshouzhi.dragon.ha.router.weight.DBSelector;
import com.tianshouzhi.dragon.ha.router.weight.ReadDBSelector;
import com.tianshouzhi.dragon.ha.router.weight.WriteDBSelector;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by TIANSHOUZHI336 on 2016/12/3.
 */
public class RealDataSourceWrapperManager {
    private static final Log LOGGER = LoggerFactory.getLogger(RealDataSourceWrapperManager.class);

    private Map<String, RealDatasourceWrapper> validDSMap = new ConcurrentHashMap<String, RealDatasourceWrapper>();

    private Map<String, RealDatasourceWrapper> invalidDsMap = new ConcurrentHashMap<String, RealDatasourceWrapper>();

    private volatile boolean isRebuiding = false;

    private Lock rebuildLock = new ReentrantLock();

    private DBSelector readDBSelector;
    private DBSelector writeDBSelector;

    public RealDataSourceWrapperManager(HashMap<String, RealDatasourceWrapper> datasourceWrapperMap) {
        refresh(datasourceWrapperMap);
        runInvalidRecoveryThread();
    }

    public void refresh(Map<String, RealDatasourceWrapper> newIndexDSMap) {
        try {
            rebuildLock.lockInterruptibly();
            isRebuiding = true;
            long start = System.currentTimeMillis();
            if (MapUtils.isEmpty(this.validDSMap) && MapUtils.isEmpty(invalidDsMap)) {
                this.validDSMap = newIndexDSMap;
            } else {
                Map<String, RealDatasourceWrapper> originDataSourceWrapperMap = getIndexDSMap();
                Map<String, RealDatasourceWrapper> needToAddMap = new HashMap<String, RealDatasourceWrapper>(4);
                Map<String, RealDatasourceWrapper> needToChangeMap = new HashMap<String, RealDatasourceWrapper>(4);

                for (Map.Entry<String, RealDatasourceWrapper> entry : newIndexDSMap.entrySet()) {
                    String newDataSourceIndex = entry.getKey();
                    RealDatasourceWrapper newDataSourceWrapper = entry.getValue();
                    if (originDataSourceWrapperMap.containsKey(newDataSourceIndex)) {
                        if (!newDataSourceWrapper.getConfig().equals(originDataSourceWrapperMap.get(newDataSourceIndex).getConfig())) {
                            needToChangeMap.put(newDataSourceIndex, newDataSourceWrapper);
                        }
                    } else {
                        needToAddMap.put(newDataSourceIndex, newDataSourceWrapper);
                    }
                }

                HashMap<String, RealDatasourceWrapper> needToRemoveMap = new HashMap<String, RealDatasourceWrapper>(4);
                for (Map.Entry<String, RealDatasourceWrapper> entry : originDataSourceWrapperMap.entrySet()) {
                    String oldDataSourceIndex = entry.getKey();
                    RealDatasourceWrapper oldDataSourceWrapper = entry.getValue();
                    if (!newIndexDSMap.containsKey(oldDataSourceIndex)) {
                        needToRemoveMap.put(oldDataSourceIndex, oldDataSourceWrapper);
                    }
                }
                //FIXME remove后，如果原来有read ，就必须还有read，原来有write，还必须有write 防止误操作
                remove(needToRemoveMap.keySet());
                add(needToAddMap);
                change(needToChangeMap);
            }
            readDBSelector = new ReadDBSelector(validDSMap);
            writeDBSelector = new WriteDBSelector(validDSMap);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            isRebuiding = false;
            rebuildLock.unlock();
        }
    }

    private void add(Map<String, RealDatasourceWrapper> indexDSMap) {
        if (MapUtils.isNotEmpty(indexDSMap)) {
            LOGGER.info("add real datasources "+indexDSMap.keySet());
            validDSMap.putAll(indexDSMap);
        }
    }

    private void remove(Set<String> datasourceIndexes) {

        if (CollectionUtils.isNotEmpty(datasourceIndexes)) {
            LOGGER.info("remove real datasource"+datasourceIndexes);
            for (String datasourceIndex : datasourceIndexes) {
                RealDatasourceWrapper realDatasourceWrapper = getIndexDSMap().get(datasourceIndex);
                if (realDatasourceWrapper != null) {
                    realDatasourceWrapper.close();
                }
                validDSMap.remove(datasourceIndex);
                invalidDsMap.remove(datasourceIndex);
            }
        }
    }

    private void change(Map<String, RealDatasourceWrapper> indexDSMap) {
        if(MapUtils.isNotEmpty(indexDSMap)){
            LOGGER.info("repalce real datasource"+indexDSMap.keySet());
            remove(indexDSMap.keySet());
            add(indexDSMap);
        }
    }

    public String selectWriteDBIndex() {
        while (!isRebuiding)
            break;
        return writeDBSelector.select();
    }

    public String selectReadDBIndex() {
        while (!isRebuiding)
            break;
        return readDBSelector.select();
    }
    public String selectReadDBIndexExclude(Set<String> excludes) {
        Set<String> managedDataSourceIndices = readDBSelector.getManagedDBIndexes();
        managedDataSourceIndices.removeAll(excludes);
        if (managedDataSourceIndices.isEmpty()) {
            return null;
        }
        return managedDataSourceIndices.iterator().next();
    }

    public RealDatasourceWrapper getDatasourceWrapperByDbIndex(String dataSourceIndex) {
        while (!isRebuiding)
            break;
        return validDSMap.get(dataSourceIndex);
    }

    public Connection getConnectionByDbIndex(String dataSourceIndex, String username, String password)
            throws SQLException {
        while (!isRebuiding)
            break;
        RealDatasourceWrapper realDatasourceWrapper = validDSMap.get(dataSourceIndex);
        if (realDatasourceWrapper == null) {
            throw new DragonHARuntimeException("not found datasouce with dataSourceIndex:" + dataSourceIndex);
        }
        DataSource realDataSource  = realDatasourceWrapper.getRealDataSource();
        Connection connection = null;

        if (StringUtils.isAnyBlank(username, password))
            connection = realDataSource.getConnection();
        else
            connection = realDataSource.getConnection(username, password);// druid不支持这个方法
        if (!connection.isReadOnly() && realDatasourceWrapper.isReadOnly()
                && validDSMap.get(dataSourceIndex).isReadOnly()) {
            connection.setReadOnly(true);
        }
        return connection;
    }

    public Connection getConnectionByDbIndex(List<String> hintDataSourceIndices, String username, String password)
            throws SQLException {
        while (!isRebuiding)
            break;
        if (hintDataSourceIndices == null && hintDataSourceIndices.size() == 0) {
            throw new SQLException("hintDataSourceIndices can't be bull or empty");
        }
        if (hintDataSourceIndices.size() == 1) {
            return getConnectionByDbIndex(hintDataSourceIndices.get(0), username, password);
        }
        int randomIndex = new Random().nextInt(hintDataSourceIndices.size());
        return getConnectionByDbIndex(hintDataSourceIndices.get(randomIndex), username, password);
    }

    private void runInvalidRecoveryThread() {
        Thread recoveryThread = new Thread("DRAGON-HA-RecoveryThread") {
            @Override
            public void run() {

                while (true) {// 存在问题... cpu使用率必然变高，改成阻塞队列
                    if (!invalidDsMap.isEmpty()) {
                        for (Map.Entry<String, RealDatasourceWrapper> entry : invalidDsMap.entrySet()) {
                            String dsIndex = entry.getKey();
                            RealDatasourceWrapper realDatasourceWrapper = entry.getValue();
                            DataSource realDataSource = (DataSource) realDatasourceWrapper.getRealDataSource();
                            try {
                                Connection connection = realDataSource.getConnection();
                                if (connection.isValid(3000)) {
                                    LOGGER.info("datasource 【"+dsIndex+"】 became valid");
                                    invalidDsMap.remove(dsIndex);
                                    validDSMap.put(dsIndex, realDatasourceWrapper);
                                    refresh(validDSMap);
                                }
                            } catch (SQLException e) {
                                LOGGER.debug("datasource 【"+dsIndex+"】 still invalid");
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
        while (!isRebuiding)
            break;
        if (validDSMap.get(dataSourceIndex) != null) {
            RealDatasourceWrapper realDatasourceWrapper = validDSMap.get(dataSourceIndex);
            LOGGER.warn("datasource 【"+dataSourceIndex+"】 became invalid");
            invalidDsMap.put(dataSourceIndex, realDatasourceWrapper);
            validDSMap.remove(dataSourceIndex);
            refresh(validDSMap);
        }
    }

    public Map<String, RealDatasourceWrapper> getIndexDSMap() {
        Map<String, RealDatasourceWrapper> map = new HashMap<String, RealDatasourceWrapper>(4);
        map.putAll(validDSMap);
        map.putAll(invalidDsMap);
        return map;
    }

    public Map<String, RealDatasourceWrapper> getValidDSMap() {
        return validDSMap;
    }

    public Map<String, RealDatasourceWrapper> getInvalidDsMap() {
        return invalidDsMap;
    }
}
