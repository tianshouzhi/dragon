package com.tianshouzhi.dragon.ha.router;

import com.tianshouzhi.dragon.common.util.MapUtils;
import com.tianshouzhi.dragon.ha.exception.DragonHAException;
import com.tianshouzhi.dragon.ha.jdbc.datasource.DragonHADatasource;
import com.tianshouzhi.dragon.ha.jdbc.datasource.RealDataSourceWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tianshouzhi on 2017/8/16.
 */
public class RouterManager {

    private final Router readRouter;
    private final Router writeRouter;
    private String haDSName;

    public RouterManager(DragonHADatasource dragonHADatasource) {
        this.haDSName = dragonHADatasource.getHADSName();
        Map<String, RealDataSourceWrapper> dataSourceWrappers = dragonHADatasource.getRealDataSourceWrapperMap();
        this.readRouter = buildRouter(dataSourceWrappers, true);
        this.writeRouter = buildRouter(dataSourceWrappers, false);
    }

    private Router buildRouter(Map<String, RealDataSourceWrapper> realDataSourceWrapperMap, boolean isRead) {

        if (MapUtils.isEmpty(realDataSourceWrapperMap)) {
            return null;
        }

        if(realDataSourceWrapperMap.size()==1){
            return buildSingleRouter(realDataSourceWrapperMap);
        }
        return buildWeightRouter(realDataSourceWrapperMap, isRead);
    }

    private Router buildSingleRouter(Map<String, RealDataSourceWrapper> configMap) {
        return new SingleRouter(this.haDSName, configMap.keySet().iterator().next());
    }

    private Router buildWeightRouter(Map<String, RealDataSourceWrapper> dataSourceWrappers, boolean isRead) {
        HashMap<String, Integer> realDSNameWeightMap = new HashMap<String, Integer>(4);
        for (Map.Entry<String, RealDataSourceWrapper> entry : dataSourceWrappers.entrySet()) {
            String realDSName = entry.getKey();
            RealDataSourceWrapper realDataSourceWrapper = entry.getValue();
            if (isRead && realDataSourceWrapper.getReadWeight() > 0) {
                realDSNameWeightMap.put(realDSName, realDataSourceWrapper.getReadWeight());
            }
            if (!isRead && realDataSourceWrapper.getWriteWeight() > 0) {
                realDSNameWeightMap.put(realDSName, realDataSourceWrapper.getWriteWeight());
            }
        }
        return new WeightRouter(this.haDSName, realDSNameWeightMap);
    }

    public String routeWrite() {
        if (writeRouter == null) {
            throw new DragonHAException("writeRouter is null");
        }
        return writeRouter.route();
    }

    public String routeRead() {
        if (readRouter == null) {
            throw new DragonHAException("readRouter is null");
        }
        return readRouter.route();
    }
}
