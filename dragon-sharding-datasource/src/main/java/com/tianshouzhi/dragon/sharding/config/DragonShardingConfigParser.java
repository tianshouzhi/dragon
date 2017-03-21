package com.tianshouzhi.dragon.sharding.config;

import com.tianshouzhi.dragon.common.initailzer.DataSourceInitailzerAdapter;
import com.tianshouzhi.dragon.sharding.route.LogicDatabase;
import com.tianshouzhi.dragon.sharding.route.LogicTable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by TIANSHOUZHI336 on 2017/3/15.
 */
public  class DragonShardingConfigParser {
    private static final Logger LOGGER= LoggerFactory.getLogger(DragonShardingConfigParser.class);
    private LogicDatabase logicDatabase;
    private HashMap<String, LogicTable> logicTableMap;
    public DragonShardingConfigParser(String configFileClassPath) throws IOException {
        if(StringUtils.isBlank(configFileClassPath)){
            throw new IllegalArgumentException("configFileClassPath can't be blank");
        }
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResource(configFileClassPath).openStream();
        Constructor constructor = new Constructor(ShardingDataSourceConfig.class);
        DumperOptions dumperOptions=new DumperOptions();
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setSplitLines(true);
        Representer representer = new Representer();
//        representer.addClassTag(LogicTableConfig.class, Tag.MAP);//会使用LogicTableConfig对map的类型进行解析
        Yaml yaml=new Yaml(constructor,representer,dumperOptions);
//        Yaml yaml=new Yaml(constructor);
        ShardingDataSourceConfig config = (ShardingDataSourceConfig) yaml.load(inputStream);
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug(yaml.dump(config));
        }
        this.logicDatabase=makeLogicDatabase(config);
        this.logicTableMap= makeLogicTableMap(config);
    }

    private  HashMap<String, LogicTable> makeLogicTableMap(ShardingDataSourceConfig config) {
        HashMap<String, LogicTable> resultMap=new HashMap<String, LogicTable>();
        LogicTableConfig defaultLogicTableConfig = config.getDefaultLogicTableConfig();
        List<LogicTableConfig> logiTableConfigList = config.getLogiTableConfigList();
        for (LogicTableConfig logicTableConfig : logiTableConfigList) {
            resultMap.put(logicTableConfig.getLogicTbName(),makeLogicTable(defaultLogicTableConfig,logicTableConfig,logicDatabase));
        }
        return resultMap;
    }

    private  LogicTable makeLogicTable(LogicTableConfig defaultLogicTableConfig, LogicTableConfig logicTableConfig,LogicDatabase logicDatabase) {
        Set<String> defaultDbRouteRules = defaultLogicTableConfig.getDbRouteRules();
        Map<String, List<String>> defaultRealDbTbMapping = defaultLogicTableConfig.getRealDbTbMapping();
        String defaultTbNameFormat = defaultLogicTableConfig.getTbNameFormat();
        Set<String> defaultTbRouteRules = defaultLogicTableConfig.getTbRouteRules();

        String logicTbName = logicTableConfig.getLogicTbName();
        if(StringUtils.isBlank(logicTbName)){
            throw new IllegalArgumentException("logicTbName can't be null");
        }

        String tbNameFormat = logicTableConfig.getTbNameFormat();
        if(StringUtils.isBlank(tbNameFormat)){
            tbNameFormat=defaultTbNameFormat.replaceAll("\\$\\{logic_table_name}",logicTbName);
        }

        Set<String> dbRouteRules=new HashSet<String>();
        if(CollectionUtils.isNotEmpty(defaultDbRouteRules)){
            dbRouteRules.addAll(defaultDbRouteRules);
        }
        if(CollectionUtils.isNotEmpty(logicTableConfig.getDbRouteRules())){
            dbRouteRules.addAll(logicTableConfig.getDbRouteRules());
        }

        Set<String> tbRouteRules=new HashSet<String>();
        if(CollectionUtils.isEmpty(defaultTbRouteRules)){
            tbRouteRules.addAll(defaultTbRouteRules);
        }
        if(CollectionUtils.isEmpty(logicTableConfig.getTbRouteRules())){
            tbRouteRules.addAll(logicTableConfig.getTbRouteRules());
        }

        Map<String,List<String>> realDbTbMapping=new HashMap<String, List<String>>();
        if(MapUtils.isEmpty(defaultRealDbTbMapping)){
            realDbTbMapping.putAll(defaultRealDbTbMapping);
        }
        if(MapUtils.isEmpty(logicTableConfig.getRealDbTbMapping())){
            realDbTbMapping.putAll(logicTableConfig.getRealDbTbMapping());
        }
        return new LogicTable(logicTbName,tbNameFormat,tbRouteRules,dbRouteRules,logicDatabase,realDbTbMapping);
    }

    private  LogicDatabase makeLogicDatabase(ShardingDataSourceConfig config) {
        String logicDSNameFormat=config.getLogicDSNameFormat();
        Set<String> dbRouteRules=config.getDefaultLogicTableConfig().getDbRouteRules();
        String realDSClass = config.getRealDSClass();
        Map<String, String> defaultDSConfig = config.getDefaultDSConfig();
        HashMap<String, DataSource> dbIndexDatasourceMap = new HashMap<String, DataSource>();
        Map<String, Map<String, String>> realDSConfigMapping = config.getRealDSConfigList();
        for (Map.Entry<String, Map<String, String>> entry : realDSConfigMapping.entrySet()) {
            String realDSName = entry.getKey();
            Map<String, String> currentConfig = entry.getValue();
            dbIndexDatasourceMap.put(realDSName,makeRealDatasouce(realDSClass,defaultDSConfig,currentConfig));
        }
        return new LogicDatabase(logicDSNameFormat, dbIndexDatasourceMap);
    }

    private  DataSource makeRealDatasouce(String realDSClass, Map<String, String> defaultDSConfig, Map<String, String> currentConfig) {
        Map<String,String> config=new HashMap<String, String>();
        if(defaultDSConfig!=null){
            config.putAll(defaultDSConfig);
        }
        config.putAll(currentConfig);//覆盖默认配置
        try {
            return DataSourceInitailzerAdapter.init(realDSClass,config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public HashMap<String, LogicTable> getLogicTableMap() {
        return logicTableMap;
    }

    public LogicDatabase getLogicDatabase() {
        return logicDatabase;
    }
}
