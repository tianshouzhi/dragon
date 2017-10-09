package com.tianshouzhi.dragon.console.modules.datasource_config.entity.dto;

import com.tianshouzhi.dragon.console.modules.datasource_config.entity.HADataSourceConfig;
import com.tianshouzhi.dragon.console.modules.datasource_config.entity.HADataSourceMapping;
import com.tianshouzhi.dragon.console.modules.mysql.entity.dto.DatabaseDTO;

import java.util.Map;

/**
 * Created by tianshouzhi on 2017/9/6.
 */
public class HADataSourceConfigDTO extends HADataSourceConfig{
    private DatabaseDTO databaseDTO;
    private Map<String,HADataSourceMapping> dataSourceMappingMap;
    private Map<String,RealDataSourceConfigDTO> realDataSourceConfigMap;

    public DatabaseDTO getDatabaseDTO() {
        return databaseDTO;
    }

    public void setDatabaseDTO(DatabaseDTO databaseDTO) {
        this.databaseDTO = databaseDTO;
    }

    public Map<String, HADataSourceMapping> getDataSourceMappingMap() {
        return dataSourceMappingMap;
    }

    public void setDataSourceMappingMap(Map<String, HADataSourceMapping> dataSourceMappingMap) {
        this.dataSourceMappingMap = dataSourceMappingMap;
    }

    public Map<String, RealDataSourceConfigDTO> getRealDataSourceConfigMap() {
        return realDataSourceConfigMap;
    }

    public void setRealDataSourceConfigMap(Map<String, RealDataSourceConfigDTO> realDataSourceConfigMap) {
        this.realDataSourceConfigMap = realDataSourceConfigMap;
    }
}
