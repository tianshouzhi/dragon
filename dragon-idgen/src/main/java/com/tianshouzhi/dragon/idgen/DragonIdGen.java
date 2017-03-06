package com.tianshouzhi.dragon.idgen;

/**
 * Created by TIANSHOUZHI336 on 2017/3/2.
 */
public interface DragonIdGen {
    /**
     * 获得作为分区字段的id
     * @return
     */
    public Long getShardingId() throws Exception;
}
