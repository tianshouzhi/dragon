package com.tianshouzhi.dragon.ha.config;

import com.tianshouzhi.dragon.common.exception.DragonConfigException;
import com.tianshouzhi.dragon.common.exception.DragonException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tianshouzhi on 2017/6/7.
 */
public class DragonHAConfigParser {
	private static JAXBContext context = null;

	private static Unmarshaller unmarshaller = null;

	private static Marshaller marshaller = null;
	static {
		try {
			context = JAXBContext.newInstance(DragonHAConfiguration.class);
			unmarshaller = context.createUnmarshaller();
			marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);//格式化输出
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	public static DragonHAConfiguration parse(InputStream config) throws DragonConfigException{
		DragonHAConfiguration configuration = null;
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();
			configuration = (DragonHAConfiguration) unmarshaller.unmarshal(config);
			return configuration;
		} catch (JAXBException e) {
			throw new DragonConfigException("parse DragonHADataSource config error", e);
		}
	}

	public static String toXml(DragonHAConfiguration configuration) throws DragonConfigException{
        StringWriter xml = new StringWriter();
        try {
            marshaller.marshal(configuration, xml);
        } catch (JAXBException e) {
            throw new DragonConfigException("unparse DragonHADataSource config error", e);
        }
        return xml.toString();
	}
}
