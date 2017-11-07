package com.tianshouzhi.dragon.console.modules.cluster.entity.dto;

import com.tianshouzhi.dragon.console.modules.cluster.entity.Database;
import com.tianshouzhi.dragon.console.modules.cluster.entity.DatabaseAccount;

/**
 * Created by tianshouzhi on 2017/9/6.
 */
public class DatabaseDTO {
    private Database database;
    private DatabaseAccount databaseAccount;

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public DatabaseAccount getDatabaseAccount() {
        return databaseAccount;
    }

    public void setDatabaseAccount(DatabaseAccount databaseAccount) {
        this.databaseAccount = databaseAccount;
    }
}
