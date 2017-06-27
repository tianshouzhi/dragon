package com.tianshouzhi.dragon.ha.config;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by tianshouzhi on 2017/6/8.
 */
public class PropertyAdapter extends XmlAdapter<ArrayList<RealDatasourceConfig.Property>,Properties>{
    @Override
    public Properties unmarshal(ArrayList<RealDatasourceConfig.Property> v) throws Exception {
        Properties properties=new Properties();
        for (RealDatasourceConfig.Property property : v) {
            properties.put(property.getName(),property.getValue());
    }
        return properties;
    }

    @Override
    public ArrayList<RealDatasourceConfig.Property> marshal(Properties v) throws Exception {
        ArrayList<RealDatasourceConfig.Property> propertyList = new ArrayList<RealDatasourceConfig.Property>();
        Enumeration<?> propertyNames = v.propertyNames();
        while (propertyNames.hasMoreElements()){
            String name = (String) propertyNames.nextElement();
            String value = v.getProperty(name);
            RealDatasourceConfig.Property property = new RealDatasourceConfig.Property();
            property.setName(name);
            property.setValue(value);
            propertyList.add(property);
        }
        return propertyList;
    }
}
