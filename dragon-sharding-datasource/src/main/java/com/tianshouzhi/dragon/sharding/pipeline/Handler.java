package com.tianshouzhi.dragon.sharding.pipeline;

import com.tianshouzhi.dragon.common.exception.DragonException;
import com.tianshouzhi.dragon.common.exception.DragonShardException;

import java.sql.SQLException;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public interface Handler {
     void invoke(HandlerContext context) throws SQLException;
}
