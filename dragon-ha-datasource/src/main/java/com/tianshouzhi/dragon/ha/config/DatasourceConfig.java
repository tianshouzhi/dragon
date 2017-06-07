package com.tianshouzhi.dragon.ha.config;

import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Properties;

/**
 * Created by tianshouzhi on 2017/6/7.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DatasourceConfig {
    @XmlAttribute(name="index",required = true) //只能用在基本数据类型上
    private String index;
    @XmlAttribute(name="readWeight",required = true)
    private Integer readWeight;
    @XmlAttribute(name="writeWeight",required = true)
    private Integer writeWeight;
    @XmlAttribute(name="realClass",required = true)
    private String realClass;
    @XmlElement(name="property") //用在复杂数据类型上
    private List<Property> properties;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public int getReadWeight() {
        return readWeight;
    }

    public void setReadWeight(int readWeight) {
        this.readWeight = readWeight;
    }

    public Integer getWriteWeight() {
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

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Property{
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
    }
}