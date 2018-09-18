package com.tianshouzhi.dragon.common.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.tianshouzhi.dragon.common.exception.DragonException;

import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.Properties;

/**
 *
 * @author tianshouzhi@126.com
 */
public abstract class VersionUtil {
	public static final String UNKNOWN_VERSION = "UNKNOWN";

	public static String getVersion(Class<?> clazz) {
		return getVersionByJarFileName(clazz);
	}

	private static String getVersionByJarFileName(Class<?> clazz) {
		String version = UNKNOWN_VERSION;
		CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
		URL location = codeSource.getLocation();
		String file = location.getFile();
		if (file.endsWith(".jar")) {
			String[] split = file.substring(0, file.indexOf(".jar")).split("-");
			version = split[1];
		}
		return version;
	}

	//jdk打包的每一个jar，META-INF目录下都有一个MANIFEST.MF文件，其中Implementation-Version就是版本号
    /* MANIFEST.MF文件内容举例
       Build-Jdk: 1.4.2_16
       Implementation-Title: Jakarta Commons Logging
       Implementation-Vendor: Apache Software Foundation
       Implementation-Vendor-Id: org.apache
       Implementation-Version: 1.1.1
    */
    private static String getVersionByMainiFest(Class<?> clazz){
        return null;
    }

	// maven打的每一个jar包，META-INF目录下都有一个pom.properties文件，记录了groupId，artifactId，和version
	public static String getVersion(String groupId, String artifactId) {
		String filename = "META-INF/maven." + groupId + "." + artifactId + "/pom.properties";
		try {
			Enumeration<URL> resources = VersionUtil.class.getClassLoader().getResources(filename);
			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();
				InputStream inputStream = url.openStream();
				Properties properties = new Properties();
				properties.load(inputStream);
				return properties.getProperty("version", UNKNOWN_VERSION);
			}
		} catch (Throwable e) {
			throw new DragonException("get version by groupId:" + groupId + ",artifactId:" + artifactId + "error!", e);
		}
		return UNKNOWN_VERSION;
	}

	public static void main(String[] args) {
		System.out.println(VersionUtil.getVersion(DruidDataSource.class));
	}
}
