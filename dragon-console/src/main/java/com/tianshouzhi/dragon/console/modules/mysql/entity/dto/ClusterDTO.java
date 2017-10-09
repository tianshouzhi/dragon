package com.tianshouzhi.dragon.console.modules.mysql.entity.dto;

import com.tianshouzhi.dragon.console.modules.mysql.entity.Cluster;
import com.tianshouzhi.dragon.console.modules.mysql.entity.ClusterInstance;
import com.tianshouzhi.dragon.console.modules.mysql.entity.Database;

import java.util.List;

/**
 * Created by tianshouzhi on 2017/9/6.
 */
public class ClusterDTO {
    private Cluster cluster;
    private List<ClusterInstance> clusterInstanceList;
    private List<Database> databaseList;

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public List<ClusterInstance> getClusterInstanceList() {
        return clusterInstanceList;
    }

    public void setClusterInstanceList(List<ClusterInstance> clusterInstanceList) {
        this.clusterInstanceList = clusterInstanceList;
    }
}
