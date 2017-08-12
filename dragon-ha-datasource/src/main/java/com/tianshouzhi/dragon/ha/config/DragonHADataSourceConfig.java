package com.tianshouzhi.dragon.ha.config;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "dragonHADatasource")
@XmlAccessorType(XmlAccessType.FIELD)
public class DragonHADataSourceConfig {
	@XmlAttribute(name = "appName", required = true)
	private String appName;

	@XmlAttribute(name = "lazyInit", required = true)
	private Boolean lazyInit =true;

	@XmlElement(name = "realDataSource", required = true)
	private List<RealDatasourceConfig> realDataSourceConfigList = new ArrayList<RealDatasourceConfig>();

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public List<RealDatasourceConfig> getRealDataSourceConfigList() {
		return realDataSourceConfigList;
	}

	public boolean isLazyInit() {
		return lazyInit;
	}

	public void setLazyInit(boolean lazyInit) {
		this.lazyInit = lazyInit;
	}

	public void setRealDataSourceConfigList(List<RealDatasourceConfig> realDataSourceConfigList) {
		this.realDataSourceConfigList = realDataSourceConfigList;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DragonHADataSourceConfig that = (DragonHADataSourceConfig) o;

		if (!appName.equals(that.appName)) return false;
		if (!lazyInit.equals(that.lazyInit)) return false;
		return realDataSourceConfigList.equals(that.realDataSourceConfigList);
	}

	@Override
	public int hashCode() {
		int result = appName.hashCode();
		result = 31 * result + lazyInit.hashCode();
		result = 31 * result + realDataSourceConfigList.hashCode();
		return result;
	}
}
