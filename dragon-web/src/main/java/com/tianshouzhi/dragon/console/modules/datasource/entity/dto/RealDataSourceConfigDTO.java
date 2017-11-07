package com.tianshouzhi.dragon.console.modules.datasource.entity.dto;

import com.tianshouzhi.dragon.console.modules.datasource.entity.RealDataSourceConfig;
import com.tianshouzhi.dragon.console.modules.cluster.entity.Instance;

/**
 * Created by tianshouzhi on 2017/9/6.
 */
public class RealDataSourceConfigDTO extends RealDataSourceConfig{
    private Instance clusterInstance;


    public Instance getClusterInstance() {
        return clusterInstance;
    }

    public void setClusterInstance(Instance clusterInstance) {
        this.clusterInstance = clusterInstance;
    }
}
