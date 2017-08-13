package com.tianshouzhi.dragon.ha.config;

import com.tianshouzhi.dragon.ha.config.parser.DragonHAXmlConfigParser;
import org.junit.Test;

import java.io.InputStream;

/**
 * Created by tianshouzhi on 2017/6/7.
 */
public class DragonHAXmlConfigParserTest {
	@Test
	public void test() throws Exception {
		InputStream stream = DragonHAXmlConfigParser.class.getClassLoader().getResourceAsStream("dragon-ha-config.xml");
		DragonHAConfiguration configuration = DragonHAXmlConfigParser.parse(stream);
		System.out.println(DragonHAXmlConfigParser.toXml(configuration));
	}
}