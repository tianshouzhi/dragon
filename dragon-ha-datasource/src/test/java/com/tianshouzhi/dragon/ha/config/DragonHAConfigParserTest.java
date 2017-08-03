package com.tianshouzhi.dragon.ha.config;

import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Created by tianshouzhi on 2017/6/7.
 */
public class DragonHAConfigParserTest {
	@Test
	public void test() throws Exception {
		InputStream stream = DragonHAConfigParser.class.getClassLoader().getResourceAsStream("dragon-ha-config.xml");
		DragonHAConfiguration configuration = DragonHAConfigParser.parse(stream);
		System.out.println(DragonHAConfigParser.toXml(configuration));
	}
}