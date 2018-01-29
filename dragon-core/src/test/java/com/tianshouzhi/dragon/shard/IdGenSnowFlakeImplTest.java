package com.tianshouzhi.dragon.shard;

import org.junit.Test;

import com.tianshouzhi.dragon.shard.idgen.IdGenSnowFlakeImpl;

/**
 * Created by tianshouzhi on 2017/7/7.
 */
public class IdGenSnowFlakeImplTest {
    @Test
    public void getAutoIncrementId() throws Exception {
        IdGenSnowFlakeImpl snowFlake = new IdGenSnowFlakeImpl(1);
        Long autoIncrementId = snowFlake.getAutoIncrementId();
        System.out.println(autoIncrementId);
    }
}