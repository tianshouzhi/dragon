package com.tianshouzhi.dragon.console.web.rest;

import com.tianshouzhi.dragon.console.domain.DragonConfig;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Dragon配置生成器
 */
@RestController
@RequestMapping("/config")
public class DragonConfigController {
    @PostMapping(value = "/create",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DragonConfig> createConfig(){
        //通用配置
        String appName;
        //数据源配置
        String datasourceNamePattern;
        int dbNums;
        String realDatasourceClass;
        Map<String,Object> defaultDatasourceConfigMap;
        Map<String,Map<String,Object>> eachDatasourceConfigMap;
        String logicTableList;
        String defaultDBName;
        return null;
    }
}
