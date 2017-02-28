package com.tianshouzhi.dragon.sharding.pipeline.handler.execution;

import com.tianshouzhi.dragon.common.jdbc.statement.DragonPrepareStatement;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingStatement;
import com.tianshouzhi.dragon.sharding.pipeline.Handler;
import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.sqlrewriter.MysqlInsertStatementRewriter;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public class ExecutionHandler implements Handler {

    @Override
    public void invoke(HandlerContext context) {
        Map<String, Map<String, MysqlInsertStatementRewriter.SplitTableStatmentInfo>> sqlRewriteResult = context.getSqlRewriteResult();
        DragonShardingStatement dragonShardingStatement = context.getDragonShardingStatement();

        boolean isPrepared=false;
        if(dragonShardingStatement instanceof PreparedStatement){
            isPrepared=true;
        }
        ExecutorService executor = DragonExecutorFactory.getInstance(context.getRouter().getLogicDatabase().getNameFormat());
        CompletionService<PreparedStatement> ecs = new ExecutorCompletionService<PreparedStatement>(executor);
        int taskNum=0;
        try {
            if (isPrepared) {
                for (Map.Entry<String, Map<String, MysqlInsertStatementRewriter.SplitTableStatmentInfo>> entry : sqlRewriteResult.entrySet()) {
                    String dbIndex = entry.getKey();
                    final DataSource ds = context.getRouter().getDataSource(dbIndex);
                    Map<String, MysqlInsertStatementRewriter.SplitTableStatmentInfo> tableSqlMap = entry.getValue();
                    Iterator<Map.Entry<String, MysqlInsertStatementRewriter.SplitTableStatmentInfo>> iterator = tableSqlMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, MysqlInsertStatementRewriter.SplitTableStatmentInfo> next = iterator.next();
                        final MysqlInsertStatementRewriter.SplitTableStatmentInfo splitTableStatement = next.getValue();
                        ecs.submit(new SqlExecutionTask(splitTableStatement,ds));
                        taskNum++;
                    }
                }
            }

           //等待所有的sql执行完成
            for (int i = 0; i < taskNum; i++) {
                PreparedStatement preparedStatement = ecs.take().get();
                int updateCount = preparedStatement.getUpdateCount();
                System.out.println(updateCount);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class SqlExecutionTask implements Callable<PreparedStatement>{
        private MysqlInsertStatementRewriter.SplitTableStatmentInfo splitTableStatement;
        DataSource ds;

        public SqlExecutionTask(MysqlInsertStatementRewriter.SplitTableStatmentInfo splitTableStatement, DataSource ds) {
            this.splitTableStatement = splitTableStatement;
            this.ds = ds;
        }

        @Override
        public PreparedStatement call() {
            PreparedStatement preparedStatement=null;
            try {
                 preparedStatement = ds.getConnection().prepareStatement(splitTableStatement.getSql().toString());
                Map<Integer, DragonPrepareStatement.ParamSetting> parameters = splitTableStatement.getParameters();
                Iterator<Map.Entry<Integer, DragonPrepareStatement.ParamSetting>> iterator = parameters.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<Integer, DragonPrepareStatement.ParamSetting> next = iterator.next();
                    Integer parameterIndex = next.getKey();
                    DragonPrepareStatement.ParamSetting paramSetting = next.getValue();
                    setPrepareStatementParams();
                }
                preparedStatement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return preparedStatement;
        }
    }

}
