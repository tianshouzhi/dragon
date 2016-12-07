package com.tianshouzhi.dragon.common;

/**
 * Created by TIANSHOUZHI336 on 2016/11/30.
 */
public interface DragonConfigSource {

    public DragonConfigSourceType getDragonConfigSourceType();

    public String getConfig();

    public void registerListener(DragonConfigListener configChangedListener);


    /**
     * 配置源类型
     * 如果同时存在本地配置和远程配置，优先使用本地配置，不支持同时使用两种配置
     */

    public static enum DragonConfigSourceType {
        LOCAL,//本地配置源
        REMOTE//远程配置源
    }
}
