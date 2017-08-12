package com.tianshouzhi.dragon.ha.config.manager;

import com.tianshouzhi.dragon.ha.config.DragonHADataSourceConfig;
import com.tianshouzhi.dragon.ha.config.parser.DragonHAXmlConfigParser;
import com.tianshouzhi.dragon.ha.exception.DragonHAConfigException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tianshouzhi on 2017/8/11.
 */
public class DragonHALocalConfigurationManager implements DragonHAConfigurationManager {
    private String configFile;
    private DragonHADataSourceConfig configuration;

    public DragonHALocalConfigurationManager(String configFile) throws DragonHAConfigException {
        this.configFile = configFile;
        InputStream inputStream =null;
        try{
            inputStream=DragonHALocalConfigurationManager.class.getClassLoader().getResourceAsStream(configFile);
            this.configuration = DragonHAXmlConfigParser.parse(inputStream);
        }finally {
            if(inputStream !=null){
                try {
                    inputStream.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    @Override
    public DragonHADataSourceConfig getConfiguration() {
        return configuration;
    }
}
