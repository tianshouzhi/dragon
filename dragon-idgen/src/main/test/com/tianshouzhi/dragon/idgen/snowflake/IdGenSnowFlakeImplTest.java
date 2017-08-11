package com.tianshouzhi.dragon.idgen.snowflake;

import org.junit.Test;

import static org.junit.Assert.*;

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