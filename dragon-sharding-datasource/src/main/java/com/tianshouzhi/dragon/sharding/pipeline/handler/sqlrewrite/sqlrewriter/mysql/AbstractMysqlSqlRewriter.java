package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.sqlrewriter.mysql;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.*;
import com.tianshouzhi.dragon.common.jdbc.statement.DragonPrepareStatement;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingPrepareStatement;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingStatement;
import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRouteInfo;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.sqlrewriter.SqlRewriter;
import com.tianshouzhi.dragon.sharding.route.LogicTable;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by TIANSHOUZHI336 on 2017/3/6.
 */
public abstract class AbstractMysqlSqlRewriter implements SqlRewriter {
    protected HandlerContext context;
    protected DragonShardingStatement dragonShardingStatement;
    protected SQLStatement sqlAst;
    protected String originSql;
    protected boolean isPrepare=false;//是否预编译,即是否实现了PreparedStatement接口
    protected  Map<Integer, DragonPrepareStatement.ParamSetting> originParameters;//isPrepare=true的情况下，传入的参数
    protected int currentParamterIndex =0;
    protected List<Object> batchExecuteInfoList;

    protected Map<String, Map<String, SqlRouteInfo>> routeMap=new HashMap<String, Map<String, SqlRouteInfo>>();
    @Override
    public Map<String, Map<String, SqlRouteInfo>> rewrite(HandlerContext context) throws SQLException {
        this.context=context;
        this.dragonShardingStatement = context.getDragonShardingStatement();
        this.sqlAst=context.getParsedSqlStatement();
        this.originSql=dragonShardingStatement.getSql();
        this.batchExecuteInfoList = dragonShardingStatement.getBatchExecuteInfoList();
        SQLStatement parsedSqlStatement = context.getParsedSqlStatement();
        if(dragonShardingStatement instanceof DragonShardingPrepareStatement){
            this.isPrepare=true;
            this.originParameters = ((DragonShardingPrepareStatement) dragonShardingStatement).getParameters();
        }
        return doRewrite(context);
    }

    protected DragonPrepareStatement.ParamSetting getParamSetting(int paramterIndex){
        if(!isPrepare){
            throw new RuntimeException("current sql is not PreparedStatement!!!");
        }
        if(originParameters ==null){
            throw new RuntimeException("no params set for sql");
        }
        if(paramterIndex> originParameters.size()){
            throw new RuntimeException("ParamterIndex>originParameters.size()");
        }
        return originParameters.get(paramterIndex);
    }

    protected abstract Map<String,Map<String,SqlRouteInfo>> doRewrite(HandlerContext context)  throws SQLException;

    //判断是否是jdbc ？占位符
    protected boolean isJdbcPlaceHolder(SQLExpr sqlExpr){
        return sqlExpr instanceof SQLVariantRefExpr;
    }
    protected List<SQLExpr> parseWhereCondition(SQLExpr where){
        List<SQLExpr> whereConditionList=new ArrayList<SQLExpr>();
        fillWhereConditionExprList(where,whereConditionList);
        return whereConditionList;
    }

    private static void fillWhereConditionExprList(SQLExpr where, List<SQLExpr> whereConditionExprList){
        if(where instanceof SQLIdentifierExpr){
            whereConditionExprList.add((SQLExpr) where.getParent());
            return;
        }
        if(where instanceof SQLInListExpr){
            whereConditionExprList.add(where);
            return;
        }
        if(where instanceof SQLBinaryOpExpr){
            SQLExpr left = ((SQLBinaryOpExpr) where).getLeft();
            SQLExpr right = ((SQLBinaryOpExpr) where).getRight();
            fillWhereConditionExprList(left,whereConditionExprList);
            fillWhereConditionExprList(right,whereConditionExprList);
        }
    }
    protected void parseSQLInListExpr(Set<String> dbTbShardColumns, Map<String, List<Object>> sqlInListConditionMap, SQLInListExpr conditionItemExpr) {
        String columnName= conditionItemExpr.getExpr().toString();
        if(dbTbShardColumns.contains(columnName)){
            List<SQLExpr> targetList = conditionItemExpr.getTargetList();
            for (SQLExpr sqlExpr : targetList) {
                if(isJdbcPlaceHolder(sqlExpr)){
                    DragonPrepareStatement.ParamSetting paramSetting = getParamSetting(++currentParamterIndex);
                    Object shardColumnValue= paramSetting.values[0];
                    List<Object> valueList = sqlInListConditionMap.get(columnName);
                    if(valueList==null){
                        valueList=new ArrayList<Object>();
                        sqlInListConditionMap.put(columnName,valueList);
                    }
                    valueList.add(shardColumnValue);
                }
            }
        }
    }

    protected void parseBinaryConditionExpr(Set<String> dbTbShardColumns, Map<String, Object> binaryShardConditionMap, SQLBinaryOpExpr conditionItemExpr) {
        SQLExpr valueExpr = conditionItemExpr.getRight();
        if(isJdbcPlaceHolder(valueExpr)){
            SQLBinaryOperator operator = conditionItemExpr.getOperator();
            String columnName = conditionItemExpr.getLeft().toString();
            DragonPrepareStatement.ParamSetting paramSetting = getParamSetting(++currentParamterIndex);
            if(dbTbShardColumns.contains(columnName)){
                if(SQLBinaryOperator.Equality==operator){
                    Object shardColumnValue=  paramSetting.values[0];
                    binaryShardConditionMap.put(columnName,shardColumnValue);
                }else{
                    throw new RuntimeException("二元操作符的分区条件，只支持=号!!!");
                }
            }
        }
    }
    protected void addRouteInfo(LogicTable logicTable, Map<String, Object> binaryShardConditionMap) {
        String routeDBIndex = logicTable.getRouteDBIndex(binaryShardConditionMap);
        String routeTBIndex = logicTable.getRouteTBIndex(binaryShardConditionMap);
        Map<String, SqlRouteInfo> dbRouteMap = routeMap.get(routeDBIndex);
        if(dbRouteMap==null){
            dbRouteMap=new HashMap<String, SqlRouteInfo>();
            routeMap.put(routeDBIndex,dbRouteMap);
        }
        SqlRouteInfo tbSqlRouteInfo = dbRouteMap.get(routeTBIndex);
        if(tbSqlRouteInfo==null){
            tbSqlRouteInfo=new SqlRouteInfo(routeDBIndex,routeTBIndex);
        }
        dbRouteMap.put(routeTBIndex,tbSqlRouteInfo);
    }
}
