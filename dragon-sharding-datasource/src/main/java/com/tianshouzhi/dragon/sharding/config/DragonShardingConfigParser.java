package com.tianshouzhi.dragon.sharding.config;

import com.tianshouzhi.dragon.common.initailzer.DataSourceInitailzerAdapter;
import com.tianshouzhi.dragon.sharding.route.LogicDataSource;
import com.tianshouzhi.dragon.sharding.route.LogicTable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created by TIANSHOUZHI336 on 2017/3/15.
 */
public  class DragonShardingConfigParser {
    private static final Logger LOGGER= LoggerFactory.getLogger(DragonShardingConfigParser.class);
    private LogicDataSource logicDataSource;
    private HashMap<String, LogicTable> logicTableMap;
    public DragonShardingConfigParser(String configFileClassPath) throws IOException {
        if(StringUtils.isBlank(configFileClassPath)){
            throw new IllegalArgumentException("configFileClassPath can't be blank");
        }
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResource(configFileClassPath).openStream();
        Constructor constructor = new Constructor(DragonShardingConfig.class);
        DumperOptions dumperOptions=new DumperOptions();
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setSplitLines(true);
        Representer representer = new Representer();
        representer.addClassTag(LogicTableConfig.class, Tag.MAP);//会使用LogicTableConfig对map的类型进行解析
        Yaml yaml=new Yaml(constructor,representer,dumperOptions);
        DragonShardingConfig config = (DragonShardingConfig) yaml.load(inputStream);
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug(yaml.dump(config));
        }
        this.logicDataSource =makeLogicDatabase(config);
        this.logicTableMap= makeLogicTableMap(config);
    }

    private  HashMap<String, LogicTable> makeLogicTableMap(DragonShardingConfig config) {
        HashMap<String, LogicTable> resultMap=new HashMap<String, LogicTable>();
        LogicTableConfig defaultLogicTableConfig = config.getDefaultLogicTableConfig();
        List<LogicTableConfig> logiTableConfigList = config.getLogiTableConfigList();
        for (LogicTableConfig logicTableConfig : logiTableConfigList) {
            resultMap.put(logicTableConfig.getLogicTbName(),makeLogicTable(defaultLogicTableConfig,logicTableConfig, logicDataSource));
        }
        return resultMap;
    }

    private  LogicTable makeLogicTable(LogicTableConfig defaultLogicTableConfig, LogicTableConfig logicTableConfig, LogicDataSource logicDataSource) {
        List<String> defaultDbRouteRules = defaultLogicTableConfig.getDbRouteRules();
        Map<String, String> defaultRealDbTbMapping = defaultLogicTableConfig.getRealDbTbMapping();
        String defaultTbNameFormat = defaultLogicTableConfig.getTbNameFormat();
        List<String> defaultTbRouteRules = defaultLogicTableConfig.getTbRouteRules();

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
        if(CollectionUtils.isNotEmpty(defaultTbRouteRules)){
            tbRouteRules.addAll(defaultTbRouteRules);
        }else if(CollectionUtils.isNotEmpty(logicTableConfig.getTbRouteRules())){
            tbRouteRules.addAll(logicTableConfig.getTbRouteRules());
        }

        Map<String,List<String>> realDbTbMapping=null;

        MessageFormat messageFomart=new MessageFormat(tbNameFormat);
        if(MapUtils.isNotEmpty(logicTableConfig.getRealDbTbMapping())){
            realDbTbMapping=caculateRealDBTBMapping(logicTableConfig.getRealDbTbMapping(), messageFomart);
        }else if(MapUtils.isNotEmpty(defaultRealDbTbMapping)){
            realDbTbMapping=caculateRealDBTBMapping(defaultRealDbTbMapping, messageFomart);
        }
        if(realDbTbMapping==null){
            throw new RuntimeException("must config realDbTbMapping");
        }

        return new LogicTable(logicTbName,tbNameFormat,tbRouteRules,dbRouteRules, logicDataSource,realDbTbMapping);
    }

    private Map<String, List<String>> caculateRealDBTBMapping(Map<String, String> defaultRealDbTbMapping, MessageFormat messageFomart) {
        Map<String, List<String>> realDbTbMapping = new HashMap<String, List<String>>();
        for (Map.Entry<String, String> entry : defaultRealDbTbMapping.entrySet()) {
            String realDBName = entry.getKey();
            String realTBRangeStr = entry.getValue();
            String[] tbIndexRange = realTBRangeStr.substring(realTBRangeStr.indexOf("[")+1, realTBRangeStr.lastIndexOf("]")).split(",");
            int start = Integer.parseInt(tbIndexRange[0]);
            int end = Integer.parseInt(tbIndexRange[1]);
            List<String> list=new ArrayList<String>();
            for (int i = start; i <= end; i++) {
                String realTBName=messageFomart.format(new Object[]{i});
                list.add(realTBName);
            }
            realDbTbMapping.put(realDBName,list);
        }
        return realDbTbMapping;
    }

    private LogicDataSource makeLogicDatabase(DragonShardingConfig config) {
        String logicDSNameFormat=config.getLogicDSNameFormat();
        String logicDSName = config.getLogicDSName();
        String realDSClass = config.getRealDSClass();
        Map<String, String> defaultDSConfig = config.getDefaultDSConfig();
        HashMap<String, DataSource> dbIndexDatasourceMap = new HashMap<String, DataSource>();
        Map<String, Map<String, String>> realDSConfigMapping = config.getRealDSConfigList();
        for (Map.Entry<String, Map<String, String>> entry : realDSConfigMapping.entrySet()) {
            String realDSName = entry.getKey();
            Map<String, String> currentConfig = entry.getValue();
            dbIndexDatasourceMap.put(realDSName,makeRealDatasouce(realDSClass,defaultDSConfig,currentConfig));
        }
        return new LogicDataSource(logicDSName,logicDSNameFormat, dbIndexDatasourceMap);
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

    public LogicDataSource getLogicDataSource() {
        return logicDataSource;
    }

    public static void main(String[] args) throws IOException {
        DragonShardingConfigParser dragonShardingConfigParser = new DragonShardingConfigParser("dragon-sharding.yml");
    }
}
