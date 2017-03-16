package com.tianshouzhi.dragon.sharding.config;

import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by TIANSHOUZHI336 on 2017/3/15.
 */
public abstract class DragonSnakeYmlConfigUtil {
    public static void loadConfig(String configFileClassPath) throws IOException {
        if(StringUtils.isBlank(configFileClassPath)){
            throw new IllegalArgumentException("configFileClassPath can't be blank");
        }
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResource(configFileClassPath).openStream();
        Yaml yaml=new Yaml();
        Object load = yaml.load(inputStream);
    }

    public static void main(String[] args) throws IOException {
        InputStream inputStream = ClassLoader.getSystemResource("dragon-sharding.yml").openStream();
        DumperOptions options = new DumperOptions();
//        options.setIndent(8);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
//        options.setCanonical(true);

        Constructor constructor = new Constructor(ShardingDataSourceConfig.class);
//        TypeDescription description = new TypeDescription(ShardingDataSourceConfig.class);
//        description.putListPropertyType("logiTableConfigList", ShardingDataSourceConfig.LogicTableYamlConfig.class);
//        constructor.addTypeDescription(description);
        Yaml yaml=new Yaml(constructor);
        Object load = yaml.load(inputStream);
        System.out.println(yaml.dump(load));
    }
}
