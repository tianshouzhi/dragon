package com.tianshouzhi.dragon.config;

/**
 * Created by TIANSHOUZHI336 on 2017/2/19.
 */
public interface DragonConfigListener {
    public void onChange(String appName,String remoteKey,String config);
}
