package com.tianshouzhi.dragon.web.modules.datasource.entity.dto;

import com.tianshouzhi.dragon.web.modules.cluster.entity.Instance;
import com.tianshouzhi.dragon.web.modules.datasource.entity.RealDataSourceConfig;

/**
 * Created by tianshouzhi on 2017/9/6.
 */
public class RealDataSourceConfigDTO extends RealDataSourceConfig {
    private Instance clusterInstance;


    public Instance getClusterInstance() {
        return clusterInstance;
    }

    public void setClusterInstance(Instance clusterInstance) {
        this.clusterInstance = clusterInstance;
    }
}
