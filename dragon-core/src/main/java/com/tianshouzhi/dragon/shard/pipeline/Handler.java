package com.tianshouzhi.dragon.shard.pipeline;

import java.sql.SQLException;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public interface Handler {
     void invoke(HandlerContext context) throws SQLException;
}
