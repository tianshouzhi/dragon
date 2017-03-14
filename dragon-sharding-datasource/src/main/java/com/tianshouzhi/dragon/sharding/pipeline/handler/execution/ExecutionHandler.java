package com.tianshouzhi.dragon.sharding.pipeline.handler.execution;

import com.tianshouzhi.dragon.common.jdbc.statement.DragonPrepareStatement;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingStatement;
import com.tianshouzhi.dragon.sharding.pipeline.Handler;
import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRouteInfo;
import org.apache.commons.collections.MapUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public class ExecutionHandler implements Handler {

    @Override
    public void invoke(HandlerContext context) {
        Map<String, Map<String, SqlRouteInfo>> sqlRouteMap = context.getSqlRouteMap();
        if(MapUtils.isNotEmpty(sqlRouteMap)){//如果不为空，则说明进行了sql
            executeBySqlRouteMap(context, sqlRouteMap);
        }else{//如果为空，判断是否要到所有DB中执行  // TODO: 2017/3/13  到底是放在这里判断，还是在rewite阶段最后来判断 ?

        }

    }

    private void executeBySqlRouteMap(HandlerContext context, Map<String, Map<String, SqlRouteInfo>> sqlRewriteResult) {
        DragonShardingStatement dragonShardingStatement = context.getDragonShardingStatement();

        boolean isPrepared=false;
        if(dragonShardingStatement instanceof PreparedStatement){
            isPrepared=true;
        }
        ExecutorService executor = DragonExecutorFactory.getInstance(context.getRouter().getLogicDatabase().getNameFormat());
        CompletionService<Void> ecs = new ExecutorCompletionService<Void>(executor);
        int taskNum=0;
        try {
            if (isPrepared) {
                for (Map.Entry<String, Map<String, SqlRouteInfo>> entry : sqlRewriteResult.entrySet()) {
                    String dbIndex = entry.getKey();
                    final DataSource ds = context.getRouter().getDataSource(dbIndex);
                    Map<String, SqlRouteInfo> tableSqlMap = entry.getValue();
                    Iterator<Map.Entry<String, SqlRouteInfo>> iterator = tableSqlMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, SqlRouteInfo> next = iterator.next();
                        final SqlRouteInfo splitTableStatement = next.getValue();
                        ecs.submit(new SqlExecutionTask(splitTableStatement,ds));
                        taskNum++;
                    }
                }
            }

            //等待所有的sql执行完成
            for (int i = 0; i < taskNum; i++) {
                ecs.take().get();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class SqlExecutionTask implements Callable<Void>{
        private SqlRouteInfo sqlRouteInfo;
        DataSource ds;

        public SqlExecutionTask(SqlRouteInfo sqlRouteInfo, DataSource ds) {
            this.sqlRouteInfo = sqlRouteInfo;
            this.ds = ds;
        }

        @Override
        public Void call() {
            PreparedStatement preparedStatement=null;
            try {
                Connection realConnection = ds.getConnection();
                preparedStatement = realConnection.prepareStatement(sqlRouteInfo.getSql().toString());
                Map<Integer, DragonPrepareStatement.ParamSetting> parameters = sqlRouteInfo.getParameters();
                Iterator<Map.Entry<Integer, DragonPrepareStatement.ParamSetting>> iterator = parameters.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<Integer, DragonPrepareStatement.ParamSetting> next = iterator.next();
                    Integer parameterIndex = next.getKey();
                    DragonPrepareStatement.ParamSetting paramSetting = next.getValue();
                    Object[] values = paramSetting.values;
                    DragonPrepareStatement.ParamType paramType = paramSetting.paramType;
                    DragonPrepareStatement.ParamType.setPrepareStatementParams(preparedStatement,parameterIndex,values,paramType);
                }
                preparedStatement.execute();
                sqlRouteInfo.setTargetStatement(preparedStatement);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }
}
