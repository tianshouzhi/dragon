package com.tianshouzhi.dragon.console.domain;

import java.util.List;

/**
 * Created by tianshouzhi on 2017/6/8.
 */
public class DragonHAConfigurationDTO {
    private int id;
    private String appName;
    private String realClass;
    private List<SingleDataSourceConfigDTO> datasourceConfigList;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getRealClass() {
        return realClass;
    }

    public void setRealClass(String realClass) {
        this.realClass = realClass;
    }

    public List<SingleDataSourceConfigDTO> getSingleDSConfigDTOList() {
        return datasourceConfigList;
    }

    public void setDatasourceConfigList(List<SingleDataSourceConfigDTO> datasourceConfigList) {
        this.datasourceConfigList = datasourceConfigList;
    }
}
