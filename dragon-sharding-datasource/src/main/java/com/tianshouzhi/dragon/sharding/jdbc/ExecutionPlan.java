package com.tianshouzhi.dragon.sharding.jdbc;

import com.tianshouzhi.dragon.ha.jdbc.statement.DragonHAStatement;

import java.util.List;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public class ExecutionPlan {
    private List<DragonHAStatement> dragonHAStatements;

    public List<DragonHAStatement> getDragonHAStatements() {
        return dragonHAStatements;
    }

    public void setDragonHAStatements(List<DragonHAStatement> dragonHAStatements) {
        this.dragonHAStatements = dragonHAStatements;
    }
}
