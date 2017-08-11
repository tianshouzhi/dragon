package com.tianshouzhi.dragon.sharding.pipeline.handler.resultmerge;

import com.tianshouzhi.dragon.sharding.pipeline.Handler;
import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import com.tianshouzhi.dragon.sharding.pipeline.handler.resultmerge.mysql.MysqlSelectResultMerger;
import com.tianshouzhi.dragon.sharding.pipeline.handler.resultmerge.mysql.MysqlUpdateResultMerger;

import java.sql.SQLException;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public class ResultMergeHandler implements Handler {
	@Override
	public void invoke(HandlerContext context) throws SQLException {
		long start = System.currentTimeMillis();
		ResultMerger resultMerger = null;
		if (!context.isQuery()) {// 如果是增删改
			resultMerger = new MysqlUpdateResultMerger();
		} else {// 如果是查询语句
			resultMerger = new MysqlSelectResultMerger();
		}
		resultMerger.merge(context);
		context.setResultMergeTimeMillis(System.currentTimeMillis() - start);
	}
}
