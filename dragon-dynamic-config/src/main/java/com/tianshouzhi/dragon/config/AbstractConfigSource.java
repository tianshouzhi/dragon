package com.tianshouzhi.dragon.config;

/**
 * Created by TIANSHOUZHI336 on 2017/2/19.
 */
public abstract class AbstractConfigSource {
    protected DragonConfigListener dragonConfigListener=null;
    public abstract String getRemoteConf(String remoteKey) throws Exception;

    public AbstractConfigSource(DragonConfigListener dragonConfigListener) {
        if(dragonConfigListener==null){
            throw new NullPointerException();
        }
        this.dragonConfigListener = dragonConfigListener;
    }
}
