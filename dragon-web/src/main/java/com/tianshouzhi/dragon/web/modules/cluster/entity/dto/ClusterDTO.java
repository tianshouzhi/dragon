package com.tianshouzhi.dragon.web.modules.cluster.entity.dto;

import com.tianshouzhi.dragon.web.modules.cluster.entity.Cluster;
import com.tianshouzhi.dragon.web.modules.cluster.entity.Instance;
import com.tianshouzhi.dragon.web.modules.cluster.entity.Database;

import java.util.List;

/**
 * Created by tianshouzhi on 2017/9/6.
 */
public class ClusterDTO {
    private Cluster cluster;
    private List<Instance> clusterInstanceList;
    private List<Database> databaseList;

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public List<Instance> getClusterInstanceList() {
        return clusterInstanceList;
    }

    public void setClusterInstanceList(List<Instance> clusterInstanceList) {
        this.clusterInstanceList = clusterInstanceList;
    }
}
