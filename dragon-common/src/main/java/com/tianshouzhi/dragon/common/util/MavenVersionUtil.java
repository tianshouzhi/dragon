package com.tianshouzhi.dragon.common.util;

import com.tianshouzhi.dragon.common.exception.DragonRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

/**
 *
 * @author tianshouzhi@126.com
 */
public abstract class MavenVersionUtil {
	public static final String UNKNOWN_VERSION = "UNKNOWN";

	public static String getVersion(Class<?> clazz) {
		String version = UNKNOWN_VERSION;
		if (clazz == null) {
			throw new NullPointerException();
		}
		CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
		URL location = codeSource.getLocation();
		String file = location.getFile();
		if (file.endsWith(".jar")) {
			String[] split = file.substring(0, file.indexOf(".jar")).split("-");
			System.out.println(Arrays.toString(split));
		}
		return version;
	}

    //maven打的每一个jar包，META-INF目录下都有一个pom.properties文件，记录了groupId，artifactId，和version
    public static String getVersion(String groupId, String artifactId) {
	    if(StringUtils.isAnyBlank(groupId,artifactId)){
	        throw new IllegalArgumentException("both groupId and artifactId can't be blank!");
        }
        String filename="META-INF/maven."+groupId+"."+artifactId+"/pom.properties";
        try{
            Enumeration<URL> resources = MavenVersionUtil.class.getClassLoader().getResources(filename);
            while (resources.hasMoreElements()){
                URL url = resources.nextElement();
                InputStream inputStream = url.openStream();
                Properties properties=new Properties();
                properties.load(inputStream);
                return properties.getProperty("version",UNKNOWN_VERSION);
            }
        }catch (Throwable e){
            throw new DragonRuntimeException("get version by groupId:"+groupId+",artifactId:"+artifactId+"error!",e);
        }
        return UNKNOWN_VERSION;
    }

	public static void main(String[] args) {
		getVersion(FastDateFormat.class);
        System.out.println(getVersion("org.slf4j", "slf4j-api"));
    }

}
