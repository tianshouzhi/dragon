package com.tianshouzhi.dragon.sharding.pipeline;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public interface Handler {
     void invoke(HandlerContext context) throws Exception;
}
