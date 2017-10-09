package com.tianshouzhi.dragon.console.modules.datasource_config.entity.dto;

import com.tianshouzhi.dragon.console.modules.datasource_config.entity.RealDataSourceConfig;
import com.tianshouzhi.dragon.console.modules.mysql.entity.ClusterInstance;

/**
 * Created by tianshouzhi on 2017/9/6.
 */
public class RealDataSourceConfigDTO extends RealDataSourceConfig{
    private ClusterInstance clusterInstance;


    public ClusterInstance getClusterInstance() {
        return clusterInstance;
    }

    public void setClusterInstance(ClusterInstance clusterInstance) {
        this.clusterInstance = clusterInstance;
    }
}
