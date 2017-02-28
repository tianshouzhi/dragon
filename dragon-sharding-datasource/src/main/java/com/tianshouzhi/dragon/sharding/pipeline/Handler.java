package com.tianshouzhi.dragon.sharding.pipeline;

import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public interface Handler {
     void invoke(HandlerContext context) throws Exception;
}
