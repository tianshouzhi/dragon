package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.sqlrewriter;

import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRewiter;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRouteInfo;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2017/2/28.
 */
public class MysqlSelectStatementRewriter implements SqlRewiter{
    @Override
    public Map<String, Map<String, SqlRouteInfo>> rewrite(HandlerContext context) throws SQLException {
        return null;
    }
}
