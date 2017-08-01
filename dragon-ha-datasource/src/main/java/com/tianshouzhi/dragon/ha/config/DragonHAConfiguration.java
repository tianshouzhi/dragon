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
	private List<RealDatasourceConfig> realDatasourceConfigList = new ArrayList<RealDatasourceConfig>();

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public List<RealDatasourceConfig> getRealDataSourceConfigList() {
		return realDatasourceConfigList;
	}

	public void setRealDatasourceConfigList(List<RealDatasourceConfig> realDatasourceConfigList) {
		this.realDatasourceConfigList = realDatasourceConfigList;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DragonHAConfiguration that = (DragonHAConfiguration) o;

		if (!appName.equals(that.appName)) return false;
		return realDatasourceConfigList.equals(that.realDatasourceConfigList);
	}

	@Override
	public int hashCode() {
		int result = appName.hashCode();
		result = 31 * result + realDatasourceConfigList.hashCode();
		return result;
	}
}
