package com.tianshouzhi.dragon.ha.config;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement(name="dragonHAConfig")
@XmlAccessorType(XmlAccessType.FIELD)
public class DragonHAConfiguration {
    @XmlAttribute(name="appName",required = true)
    private String appName;
    @XmlElement(name="dataSourceConfig",required = true)
    private List<DatasourceConfig> dsConfigList=new ArrayList<DatasourceConfig>();

    public String getAppName() {
        return appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }

    public List<DatasourceConfig> getDsConfigList() {
        return dsConfigList;
    }

    public void setDsConfigList(List<DatasourceConfig> dsConfigList) {
        this.dsConfigList = dsConfigList;
    }
}
