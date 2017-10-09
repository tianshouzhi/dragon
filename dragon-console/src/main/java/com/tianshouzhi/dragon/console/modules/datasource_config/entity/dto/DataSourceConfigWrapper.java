package com.tianshouzhi.dragon.console.modules.datasource_config.entity.dto;

import com.tianshouzhi.dragon.console.modules.datasource_config.entity.AppConfig;
import com.tianshouzhi.dragon.console.modules.mysql.entity.DatabaseAccount;
import com.tianshouzhi.dragon.console.modules.datasource_config.entity.BaseDataSourceConfig;

/**
 * Created by tianshouzhi on 2017/9/6.
 */
public class DataSourceConfigWrapper {
    private AppConfig appConfig;
    private DatabaseAccount databaseAccount;
    private BaseDataSourceConfig dataSourceConfig;

    public AppConfig getAppConfig() {
        return appConfig;
    }

    public void setAppConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public DatabaseAccount getDatabaseAccount() {
        return databaseAccount;
    }

    public void setDatabaseAccount(DatabaseAccount databaseAccount) {
        this.databaseAccount = databaseAccount;
    }

    public BaseDataSourceConfig getDataSourceConfig() {
        return dataSourceConfig;
    }

    public void setDataSourceConfig(BaseDataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }
}
