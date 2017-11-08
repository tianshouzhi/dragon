package com.tianshouzhi.dragon.common.domain;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * Created by tianshouzhi on 2017/8/21.
 */
public class UserDTO {
//    private int id;
//    private String dsName;

    public void setId(int id) {
    }

    public String getName() {
        return null;
    }



    public static void main(String[] args) throws IntrospectionException {
//        BeanUtils.copyProperties(user,userDto);
        test1();
    }

    private static void test1() throws IntrospectionException {
        //得到bean的所有属性
        BeanInfo info=Introspector.getBeanInfo(UserDTO.class);

        //得到bean自己的属性，砍掉从继承父类的属性
//        BeanInfo info= Introspector.getBeanInfo(UserDTO.class,Object.class);

        //得到所有的属性的属性描述器
        PropertyDescriptor[] pds=info.getPropertyDescriptors();

        for(PropertyDescriptor pd:pds){
            Method readMethod = pd.getReadMethod();
            Method writeMethod = pd.getWriteMethod();
            System.out.println(pd.getName());
        }
    }

}
