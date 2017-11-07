package com.tianshouzhi.dragon.common.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by tianshouzhi on 2017/10/14.
 */
public abstract class BeanPropertyUtil {
	public static void populate(Object obj, Properties properties) throws Exception {
		if (obj == null || properties == null) {
			throw new NullPointerException();
		}
		Enumeration<?> propertyNames = properties.propertyNames();
		while (propertyNames.hasMoreElements()) {
			String propertyName = (String) propertyNames.nextElement();
			String value = properties.getProperty(propertyName);
			setProperty(obj, propertyName, value);
		}
	}

	private static void setProperty(Object obj, String propertyName, String strValue)
	      throws InvocationTargetException, IllegalAccessException, IntrospectionException {
		PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, obj.getClass());
		Method writeMethod = propertyDescriptor.getWriteMethod();
		Class<?>[] parameterTypes = writeMethod.getParameterTypes();
		if (parameterTypes == null || parameterTypes.length != 1) {

		}

		Class<?> parameterType = parameterTypes[0];
		Object value = null;
		if ((parameterType.isAssignableFrom(String.class))) {
			value = strValue;
		}
		if ((parameterType.isAssignableFrom(Integer.class))) {
			value = Integer.parseInt(strValue);
		}
		if ((parameterType.isAssignableFrom(Long.class))) {
			value = Long.parseLong(strValue);
		}
		if ((parameterType.isAssignableFrom(Short.class))) {
			value = Short.parseShort(strValue);
		}
		if ((parameterType.isAssignableFrom(Byte.class))) {
			value = Byte.parseByte(strValue);
		}
		if ((parameterType.isAssignableFrom(Double.class))) {
			value = Double.parseDouble(strValue);
		}
		if ((parameterType.isAssignableFrom(Float.class))) {
			value = Float.parseFloat(strValue);
		}
		if ((parameterType.isAssignableFrom(Boolean.class))) {
			value = Boolean.parseBoolean(strValue);
		}
		writeMethod.invoke(obj, value);
	}
}
