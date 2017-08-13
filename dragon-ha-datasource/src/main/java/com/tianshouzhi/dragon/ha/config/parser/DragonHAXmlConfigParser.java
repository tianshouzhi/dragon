package com.tianshouzhi.dragon.ha.config.parser;

import com.tianshouzhi.dragon.ha.config.DragonHAConfiguration;
import com.tianshouzhi.dragon.ha.exception.DragonHAConfigException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

/**
 * Created by tianshouzhi on 2017/6/7.
 */
public class DragonHAXmlConfigParser {
	private static JAXBContext context = null;

	private static Unmarshaller unmarshaller = null;

	private static Marshaller marshaller = null;
	static {
		try {
			context = JAXBContext.newInstance(DragonHAConfiguration.class);
			unmarshaller = context.createUnmarshaller();
			marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);// 格式化输出
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static DragonHAConfiguration parse(String xmlConfig) throws DragonHAConfigException {
		return parse(new ByteArrayInputStream(xmlConfig.getBytes(Charset.forName("UTF-8"))));
	}

	public static DragonHAConfiguration parse(InputStream config) throws DragonHAConfigException {
		DragonHAConfiguration configuration = null;
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();
			configuration = (DragonHAConfiguration) unmarshaller.unmarshal(config);
			return configuration;
		} catch (JAXBException e) {
			throw new DragonHAConfigException("parse DragonHADataSource config error", e);
		}finally {
			try {
				config.close();
			} catch (IOException ignore) {
			}
		}
	}

	public static String toXml(DragonHAConfiguration configuration) throws DragonHAConfigException {
		StringWriter xml = new StringWriter();
		try {
			marshaller.marshal(configuration, xml);
		} catch (JAXBException e) {
			throw new DragonHAConfigException("unparse DragonHADataSource config error", e);
		}
		return xml.toString();
	}
}
