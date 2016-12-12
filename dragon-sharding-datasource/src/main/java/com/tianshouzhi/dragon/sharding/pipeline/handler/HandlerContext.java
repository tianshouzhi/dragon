package com.tianshouzhi.dragon.sharding.pipeline.handler;

import com.tianshouzhi.dragon.ha.jdbc.statement.DragonHAStatement;
import com.tianshouzhi.dragon.sharding.jdbc.DragonShardingStatement;
import com.tianshouzhi.dragon.sharding.jdbc.ExecutionPlan;

import java.util.Set;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public class HandlerContext {
    /**
     * 所有的参数都包含在这个对象中，不需要解析那么多
     */
    DragonShardingStatement dragonShardingStatement;
    private ExecutionPlan exectionPlan;
    private Set<DragonHAStatement> dragonHAStatements;

    public HandlerContext(DragonShardingStatement dragonShardingStatement) {
        this.dragonShardingStatement = dragonShardingStatement;
    }

    public ExecutionPlan getExectionPlan() {
        return exectionPlan;
    }

    public void setExectionPlan(ExecutionPlan exectionPlan) {
        this.exectionPlan = exectionPlan;
    }

    public ExecutionPlan getExecutionPlan() {
        return exectionPlan;
    }

    public Set<DragonHAStatement> getDragonHAStatements() {
        return dragonHAStatements;
    }

    public void setDragonShardingStatement(DragonShardingStatement dragonShardingStatement) {
        this.dragonShardingStatement = dragonShardingStatement;
    }

    public DragonShardingStatement getDragonShardingStatement() {
        return dragonShardingStatement;
    }

    public void setDragonHAStatements(Set<DragonHAStatement> dragonHAStatements) {
        this.dragonHAStatements = dragonHAStatements;
    }
}
