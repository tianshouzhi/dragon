package com.tianshouzhi.dragon.dynamic.config;

/**
 * Created by TIANSHOUZHI336 on 2017/2/19.
 */
public interface DragonDynamicConfigSource {
    public String getConfig(String remoteKey);
    public void addListener();
}
