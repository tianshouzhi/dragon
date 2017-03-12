package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite;

import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2017/2/28.
 */
public interface SqlRewriter {
    public Map<String,Map<String,SqlRouteInfo>> rewrite(HandlerContext context) throws SQLException;
}
