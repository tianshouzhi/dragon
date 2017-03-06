package com.tianshouzhi.dragon.sharding.pipeline;

import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingStatement;
import com.tianshouzhi.dragon.sharding.pipeline.handler.execution.ExecutionHandler;
import com.tianshouzhi.dragon.sharding.pipeline.handler.hint.HintParseHandler;
import com.tianshouzhi.dragon.sharding.pipeline.handler.resultmerge.ResultMergeHandler;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlparse.SqlParseHandler;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRewriteHandler;
import com.tianshouzhi.dragon.sharding.route.Router;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TIANSHOUZHI336 on 2017/2/23.
 */
public class Pipeline {
    private List<Handler> handlerChains;
    private HandlerContext handlerContext;

    public Pipeline(DragonShardingStatement dragonShardingStatement, Router router) {
        this.handlerContext = new HandlerContext(dragonShardingStatement);
        this.handlerChains = new ArrayList<Handler>();
        this.handlerChains.add(new HintParseHandler());
        this.handlerChains.add(new SqlParseHandler());
        this.handlerChains.add(new SqlRewriteHandler());
        this.handlerChains.add(new ExecutionHandler());
        this.handlerChains.add(new ResultMergeHandler());
    }

    //执行调用链
    public void execute() {
        for (Handler handler : handlerChains) {
            try {
                handler.invoke(handlerContext);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public HandlerContext getHandlerContext() {
        return handlerContext;
    }
}
