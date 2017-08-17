package com.tianshouzhi.dragon.ha.config;

import javax.xml.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tianshouzhi on 2017/6/7.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RealDatasourceConfig {
	@XmlAttribute(name = "index", required = true) // 只能用在基本数据类型上
	private String index;

	@XmlAttribute(name = "readWeight", required = true)
	private int readWeight = -1;

	@XmlAttribute(name = "writeWeight", required = true)
	private int writeWeight = -1;

	@XmlAttribute(name = "realClass", required = true)
	private String realClass;

	@XmlElement(name = "property") // 用在复杂数据类型上
	private List<Property> properties;

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public int getWriteWeight() {
		return writeWeight;
	}

	public void setWriteWeight(Integer writeWeight) {
		this.writeWeight = writeWeight;
	}

	public String getRealClass() {
		return realClass;
	}

	public void setRealClass(String realClass) {
		this.realClass = realClass;
	}

	public void setReadWeight(Integer readWeight) {
		this.readWeight = readWeight;
	}

	public int getReadWeight() {
		return readWeight;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Property {
		@XmlAttribute
		private String name;

		@XmlAttribute
		private String value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return "Property{" + "name='" + name + '\'' + ", value='" + value + '\'' + '}';
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			Property property = (Property) o;

			if (!name.equals(property.name))
				return false;
			return value.equals(property.value);
		}

		@Override
		public int hashCode() {
			int result = name.hashCode();
			result = 31 * result + value.hashCode();
			return result;
		}
	}

	public Map<String, String> getPropertiesMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		for (RealDatasourceConfig.Property property : this.properties) {
			map.put(property.getName(), property.getValue());
		}
		return map;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		RealDatasourceConfig that = (RealDatasourceConfig) o;

		if (readWeight != that.readWeight)
			return false;
		if (writeWeight != that.writeWeight)
			return false;
		if (!index.equals(that.index))
			return false;
		if (!realClass.equals(that.realClass))
			return false;
		return properties.equals(that.properties);
	}

	@Override
	public int hashCode() {
		int result = index.hashCode();
		result = 31 * result + readWeight;
		result = 31 * result + writeWeight;
		result = 31 * result + realClass.hashCode();
		result = 31 * result + properties.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "RealDatasourceConfig{" + "index='" + index + '\'' + ", readWeight=" + readWeight + ", writeWeight="
		      + writeWeight + ", realClass='" + realClass + '\'' + ", properties=" + properties + '}';
	}
}
