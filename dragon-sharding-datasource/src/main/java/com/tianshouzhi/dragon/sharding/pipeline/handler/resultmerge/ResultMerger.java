package com.tianshouzhi.dragon.sharding.pipeline.handler.resultmerge;

import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;

import java.sql.SQLException;

/**
 * Created by TIANSHOUZHI336 on 2017/3/12.
 */
public interface ResultMerger {
    public void merge(HandlerContext context) throws SQLException;
}
