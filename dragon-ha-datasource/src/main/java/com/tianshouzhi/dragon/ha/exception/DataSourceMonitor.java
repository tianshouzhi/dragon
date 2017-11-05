package com.tianshouzhi.dragon.ha.exception;

import com.tianshouzhi.dragon.common.log.Log;
import com.tianshouzhi.dragon.common.log.LoggerFactory;
import com.tianshouzhi.dragon.common.thread.DragonThreadFactory;
import com.tianshouzhi.dragon.ha.jdbc.datasource.RealDataSourceWrapper;
import com.tianshouzhi.dragon.ha.util.DatasourceUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by tianshouzhi on 2017/9/22.
 */
public abstract class DataSourceMonitor {
    private static final Log LOG = LoggerFactory.getLogger(DataSourceMonitor.class);
    public static Map<String, Map<String, RealDataSourceWrapper>> unavailableDataSources = new ConcurrentHashMap<String, Map<String, RealDataSourceWrapper>>();

    private static ScheduledExecutorService monitorExecutor = new ScheduledThreadPoolExecutor(1, new DragonThreadFactory("DRAGON_FATAL_EXCEPTION_DATASOURCE_CHECKER",true));

    static {
        monitorExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (unavailableDataSources != null) {
                    for (Map.Entry<String, Map<String, RealDataSourceWrapper>> haDatasourceEntry : unavailableDataSources.entrySet()) {
                        String haDSName = haDatasourceEntry.getKey();
                        for (Map.Entry<String, RealDataSourceWrapper> realDataSourceEntry : haDatasourceEntry.getValue().entrySet()) {
                            RealDataSourceWrapper dataSourceWrapper = realDataSourceEntry.getValue();
                            try {
                                Connection connection = dataSourceWrapper.getConnection();
                                String realDSName = dataSourceWrapper.getRealDSName();

                                if (connection.isValid(3)) {
                                    dataSourceWrapper.enable();


                                    LOG.info("the real datasource " + realDSName + " managed by DragonHADatasource[" + haDSName + "] managed " + "become not available!!!");
                                }
                                DatasourceUtil.close(haDSName,realDSName,connection);
                            } catch (SQLException ignore) {
                            }
                        }
                    }
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public static void monitor(SQLException e, String hADataSourceIndex, RealDataSourceWrapper realDataSourceWrapper) {
        if (ExceptionSorterUtil.isExceptionFatal(e)) {
            realDataSourceWrapper.disable();
            addToMonitor(hADataSourceIndex, realDataSourceWrapper);
            LOG.warn("the real datasource " + realDataSourceWrapper.getRealDSName() + " managed by DragonHADatasource[" + hADataSourceIndex + "] managed " + "become not available!!!");
        }
    }

    private static void addToMonitor(String dataSourceHashCode, RealDataSourceWrapper realDataSourceWrapper) {
        Map<String, RealDataSourceWrapper> unavailableDataSourceMap = unavailableDataSources.get(dataSourceHashCode);
        if (unavailableDataSourceMap == null) {
            synchronized (DataSourceMonitor.class) {
                if (unavailableDataSourceMap == null) {
                    unavailableDataSourceMap = new ConcurrentHashMap<String, RealDataSourceWrapper>();
                    unavailableDataSources.put(dataSourceHashCode, unavailableDataSourceMap);
                }
            }
        }
        unavailableDataSourceMap.put(realDataSourceWrapper.getRealDSName(), realDataSourceWrapper);
    }

    public static boolean isAvailable(String haDSName, RealDataSourceWrapper realDataSourceWrapper) {
        Map<String, RealDataSourceWrapper> invalidDsMap = unavailableDataSources.get(haDSName);
        if (invalidDsMap == null || !invalidDsMap.containsKey(realDataSourceWrapper.getRealDSName())) {
            return true;
        }
        return false;
    }

    public static Set<String> getInvalidRealDs(String haDataSourceName) {
        Map<String, RealDataSourceWrapper> invalidDsMap = unavailableDataSources.get(haDataSourceName);
        if (invalidDsMap == null) {
            return null;
        }
        return invalidDsMap.keySet();
    }
}
