package com.tianshouzhi.dragon.idgen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by TIANSHOUZHI336 on 2017/3/2.
 */
public abstract class AbstractDragonIdGen implements DragonIdGen{
    public static final Logger LOGGER=LoggerFactory.getLogger(AbstractDragonIdGen.class);

    protected boolean isInit=false;

    private void init(){
        if(isInit){
            return;
        }
        synchronized (this){
            if(isInit){
                return;
            }
            doInit();
            isInit=true;

        }

    }

    protected abstract void doInit();

    @Override
    public Long getShardingId() {
        init();
        Long id = getId();
        String randomDBIndex = getRandomDBIndex();
        String randomTBIndex = getRandomTBIndex();
        long shardingId = Long.parseLong(id + randomDBIndex + randomTBIndex);
        LOGGER.debug("generate sharding id:{}",shardingId);
        return shardingId;
    }

    protected abstract String getRandomDBIndex();
    protected abstract String getRandomTBIndex();
    protected abstract Long getId();
}
