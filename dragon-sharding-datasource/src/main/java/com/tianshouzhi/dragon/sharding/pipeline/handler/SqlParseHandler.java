package com.tianshouzhi.dragon.sharding.pipeline.handler;

import com.tianshouzhi.dragon.common.jdbc.statement.DragonStatement;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingStatement;

import java.util.List;

/**
 * 解析出sql中的参数和参数值(主要是找出分区字段和分区字段的值)
 */
public class SqlParseHandler implements Handler{
    @Override
    public void invoke(HandlerContext context) {
     DragonShardingStatement dragonShardingStatement = context.getDragonShardingStatement();
        DragonStatement.ExecuteType executeType = dragonShardingStatement.getExecuteType();
        if(executeType== DragonStatement.ExecuteType.EXECUTE_BATCH){//批处理
            List<Object> batchExecuteInfoList = dragonShardingStatement.getBatchExecuteInfoList();
            if(!batchExecuteInfoList.isEmpty()){
                for (Object o : batchExecuteInfoList) {
                    if(o instanceof String){

                    }
                }
            }
        }else{//非批处理
            String sql = dragonShardingStatement.getSql();

        }
    }
}
