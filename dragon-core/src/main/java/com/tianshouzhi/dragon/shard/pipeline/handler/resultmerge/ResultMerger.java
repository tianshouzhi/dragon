package com.tianshouzhi.dragon.shard.pipeline.handler.resultmerge;

import com.tianshouzhi.dragon.shard.pipeline.HandlerContext;

import java.sql.SQLException;

/**
 * Created by TIANSHOUZHI336 on 2017/3/12.
 */
public interface ResultMerger {
	public void merge(HandlerContext context) throws SQLException;
}
