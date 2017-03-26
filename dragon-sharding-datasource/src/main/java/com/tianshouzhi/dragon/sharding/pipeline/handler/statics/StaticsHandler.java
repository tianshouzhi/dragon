package com.tianshouzhi.dragon.sharding.pipeline.handler.statics;

import com.alibaba.fastjson.JSON;
import com.tianshouzhi.dragon.common.jdbc.statement.DragonPrepareStatement;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingPrepareStatement;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingStatement;
import com.tianshouzhi.dragon.sharding.pipeline.Handler;
import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRouteInfo;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * 统计sql执行信息
 */
public class StaticsHandler implements Handler{
    private static final Logger LOGGER= LoggerFactory.getLogger(StaticsHandler.class);
    @Override
    public void invoke(HandlerContext context) throws Exception {
        if(true){
            return;
        }
        DragonShardingStatement dragonShardingStatement = context.getDragonShardingStatement();
        //原始sql执行时间
        String originSql = dragonShardingStatement.getSql();
        //是否是查询
        boolean query = context.isQuery();
        boolean success=true;
        String exception=null;
        if(context.getThrowable()!=null){
            success=false;
            StringWriter out = new StringWriter();
            context.getThrowable().printStackTrace(new PrintWriter(out));
            exception=out.toString();
        }
        //原始sql带的参数
        boolean isPrepare=false;
        Object[] originParamters=null;
        if(dragonShardingStatement instanceof DragonShardingPrepareStatement){
            isPrepare=true;
            Map<Integer, DragonPrepareStatement.ParamSetting> parameters = ((DragonShardingPrepareStatement) dragonShardingStatement).getParameters();
            if(MapUtils.isNotEmpty(parameters)){
                originParamters=new Object[parameters.size()];
                for (Map.Entry<Integer, DragonPrepareStatement.ParamSetting> entry : parameters.entrySet()) {
                    Integer position = entry.getKey();
                    Object[] values = entry.getValue().values;
                    originParamters[position-1]= Arrays.toString(values);
                }
            }
        }
        //更新记录数
        int totalUpdateCount=context.getTotalUpdateCount();
        //原始查询结果集记录数
        int originQueryCount=context.getOriginQueryCount();
        //过滤后，即结果集合并后的记录数
        int returnRowCount=context.getMergedResultSet().size();
        //sql开始执行的时间
        long beginTime = context.getBeginTime();
        //总执行时间
        long totalExecuteTime = System.currentTimeMillis()- beginTime;
        //是否命中sql parser缓存
        boolean hitSqlParserCache=context.isHitSqlParserCache();
        //sql解析花费的时间
        long sqlParseTimeMillis=context.getSqlParseTimeMillis();
        //sql重写与路由花费的时间
        long sqlRewriteTimeMillis = context.getSqlRewriteTimeMillis();
        //并行执行的任务数，表示使用了多少个连接，如果不使用事务，则与routeSqlNums相等，如果使用了事务，则routeSqlNums<=routeSqlNums
        int parallelExecutionTaskNum=context.getParallelExecutionTaskNum();
        //并行执行花费的时间
        long parallelExecutionTimeMillis = context.getParallelExecutionTimeMillis();
        //合并结果集花费的时间
        long resultMergeTimeMillis = context.getResultMergeTimeMillis();
        //路由后sql的个数
        long routeSqlNums=0;
        //详细路由信息
        Map<String, Map<String, SqlRouteInfo>> sqlRouteMap = context.getSqlRouteMap();
        Map<String,List<SqlRouteDetail>> routeDetailMap=new HashMap<String, List<SqlRouteDetail>>();
        for (Map.Entry<String, Map<String, SqlRouteInfo>> entry : sqlRouteMap.entrySet()) {
            String realDB = entry.getKey();
            Map<String, SqlRouteInfo> routeTables = entry.getValue();
            for (Map.Entry<String, SqlRouteInfo> routeInfoEntry : routeTables.entrySet()) {
                SqlRouteInfo sqlRouteInfo = routeInfoEntry.getValue();
                String sql = sqlRouteInfo.getSql();
                long executionTimeMillis = sqlRouteInfo.getExecutionTimeMillis();
                List<SqlRouteDetail> sqlRouteDetailses = routeDetailMap.get(realDB);
                if(sqlRouteDetailses==null){
                    sqlRouteDetailses=new ArrayList<SqlRouteDetail>();
                    routeDetailMap.put(realDB,sqlRouteDetailses);
                }
                routeSqlNums++;
                sqlRouteDetailses.add(new SqlRouteDetail(sql,executionTimeMillis));
            }
        }
        String routeDetails= JSON.toJSONString(routeDetailMap,true);
        SqlExecutionStatics sqlExecutionStatics=new SqlExecutionStatics();
        sqlExecutionStatics.setOriginSql(originSql);
        sqlExecutionStatics.setOriginParamters(originParamters);
        sqlExecutionStatics.setQuery(query);
        sqlExecutionStatics.setPrepare(isPrepare);
        sqlExecutionStatics.setSuccess(success);
        sqlExecutionStatics.setException(exception);
        sqlExecutionStatics.setTotalUpdateCount(totalUpdateCount);
        sqlExecutionStatics.setReturnRowCount(returnRowCount);
        sqlExecutionStatics.setQueryRowCount(originQueryCount);
        sqlExecutionStatics.setRouteSqlNums(routeSqlNums);
        sqlExecutionStatics.setBeginTime(beginTime);
        sqlExecutionStatics.setTotalExecuteTime(totalExecuteTime);
        sqlExecutionStatics.setHitSqlParserCache(hitSqlParserCache);
        sqlExecutionStatics.setSqlParseTimeMillis(sqlParseTimeMillis);
        sqlExecutionStatics.setSqlRewriteTimeMillis(sqlRewriteTimeMillis);
        sqlExecutionStatics.setParallelExecutionTaskNum(parallelExecutionTaskNum);
        sqlExecutionStatics.setParallelExecutionTimeMillis(parallelExecutionTimeMillis);
        sqlExecutionStatics.setResultMergeTimeMillis(parallelExecutionTimeMillis);
        sqlExecutionStatics.setRouteDetails(routeDetails);
        sqlExecutionStatics.setLogicDataBaseName(context.getDragonShardingConfig().getAppName());
        LOGGER.debug("sql execute details:\n{}",JSON.toJSONString(sqlExecutionStatics,true));
    }


    public static class SqlRouteDetail {
        private String sql;
        private long executionTimeMillis;

        public SqlRouteDetail(String sql, long executionTimeMillis) {
            this.sql = sql;
            this.executionTimeMillis = executionTimeMillis;
        }

        public long getExecutionTimeMillis() {
            return executionTimeMillis;
        }
        public String getSql() {
            return sql;
        }
    }
}
