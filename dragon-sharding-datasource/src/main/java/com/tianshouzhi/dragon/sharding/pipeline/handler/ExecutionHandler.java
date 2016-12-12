package com.tianshouzhi.dragon.sharding.pipeline.handler;

import com.tianshouzhi.dragon.ha.jdbc.statement.DragonHAStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public class ExecutionHandler implements Handler{
    ExecutorService executorService=null;
    @Override
    public void invoke(HandlerContext context) {

        Set<DragonHAStatement> DragonHAStatements=context.getDragonHAStatements();
        List<Callable<Boolean>> callableList=new ArrayList<Callable<Boolean>>();
        for (DragonHAStatement dragonHAStatement : DragonHAStatements) {
            DispatchCallable task = new DispatchCallable(dragonHAStatement);
            callableList.add(task);
        }

        try {
            List<Future<Boolean>> futures = executorService.invokeAll(callableList);//等待所有的sql执行完成
        } catch (InterruptedException e) {
//            throw new DragonException(e);
        }
    }

    private static class DispatchCallable implements Callable<Boolean>{
        DragonHAStatement dragonHAStatement;

        public DispatchCallable(DragonHAStatement dragonHAStatement) {
            this.dragonHAStatement = dragonHAStatement;
        }

        @Override
        public Boolean call() throws Exception {
            return dragonHAStatement.doExecute();
        }
    }

}
