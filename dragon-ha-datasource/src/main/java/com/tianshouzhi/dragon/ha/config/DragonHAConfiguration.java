package com.tianshouzhi.dragon.ha.config;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "dragonHADatasource")
@XmlAccessorType(XmlAccessType.FIELD)
public class DragonHAConfiguration {
	@XmlAttribute(name = "appName", required = true)
	private String appName;

	@XmlElement(name = "realDatasource", required = true)
	private List<RealDatasourceConfig> dsConfigList = new ArrayList<RealDatasourceConfig>();

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public List<RealDatasourceConfig> getDsConfigList() {
		return dsConfigList;
	}

	public void setDsConfigList(List<RealDatasourceConfig> dsConfigList) {
		this.dsConfigList = dsConfigList;
	}
}
