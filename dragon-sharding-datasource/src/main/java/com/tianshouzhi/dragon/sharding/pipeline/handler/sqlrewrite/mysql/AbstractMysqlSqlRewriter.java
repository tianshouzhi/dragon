package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.mysql;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.tianshouzhi.dragon.common.jdbc.statement.DragonPrepareStatement;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingPrepareStatement;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingStatement;
import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRewriter;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRouteInfo;
import com.tianshouzhi.dragon.sharding.route.LogicTable;
import org.apache.commons.collections.MapUtils;

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
    protected List<LogicTable> parsedLogicTableList;

    @Override
    public void rewrite(HandlerContext context) throws SQLException {
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
        doRewrite(context);
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

    protected abstract void doRewrite(HandlerContext context)  throws SQLException;

    protected void parseLogicTableList(SQLTableSource tableSource) {
         parsedLogicTableList=new LinkedList<LogicTable>();
        if(tableSource instanceof SQLExprTableSource){
            String logicTableName = ((SQLExprTableSource) tableSource).getExpr().toString();
            LogicTable logicTable = context.getLogicTable(logicTableName);
            parsedLogicTableList.add(logicTable);

        }
        if(tableSource instanceof SQLJoinTableSource){
            SQLTableSource left = ((SQLJoinTableSource) tableSource).getLeft();
            SQLTableSource right = ((SQLJoinTableSource) tableSource).getRight();
            if(!(left instanceof SQLExprTableSource)||!(right instanceof SQLExprTableSource)){
                //join 查询的两个表，不能是其他类型的表
                throw new UnsupportedOperationException("join query only support simple table!");
            }
            String leftLogicTableName = ((SQLExprTableSource) left).getExpr().toString();
            LogicTable leftLogicTable = context.getLogicTable(leftLogicTableName);
            String rightLogicTableName = ((SQLExprTableSource) right).getExpr().toString();
            LogicTable rightLogicTable = context.getLogicTable(rightLogicTableName);
            parsedLogicTableList.add(leftLogicTable);
            parsedLogicTableList.add(rightLogicTable);
            return ;
        }
        if(tableSource instanceof SQLUnionQueryTableSource){

        }
        if(tableSource instanceof SQLSubqueryTableSource){

        }

    }
    //判断是否是jdbc ？占位符
    protected boolean isJdbcPlaceHolder(SQLExpr sqlExpr){
        if(sqlExpr==null){
            return false;
        }
        return sqlExpr instanceof SQLVariantRefExpr;
    }
    protected List<SQLExpr> parseWhereConditionList(SQLExpr where){
        List<SQLExpr> whereConditionList=new ArrayList<SQLExpr>();
        fillWhereConditionExprList(where,whereConditionList);
        return whereConditionList;
    }

    private static void fillWhereConditionExprList(SQLExpr where, List<SQLExpr> whereConditionExprList){
        if(where instanceof SQLIdentifierExpr//直接列名的情况
                ||where instanceof SQLPropertyExpr){//表名.列名的情况
            SQLExpr parent = (SQLExpr) where.getParent();
            if(!whereConditionExprList.contains(parent)){
                whereConditionExprList.add(parent);
            }
            return;
        }
        if(where instanceof SQLInListExpr){
            whereConditionExprList.add(where);
            return;
        }
        if(where instanceof SQLBinaryOpExpr){
            SQLExpr left = ((SQLBinaryOpExpr) where).getLeft();
            SQLExpr right = ((SQLBinaryOpExpr) where).getRight();
            if(right instanceof SQLPropertyExpr){//不解析关联查询条件，例如emp.dept_id=dept.id，这样的条件对于分区没有意义
                return;
            }
            fillWhereConditionExprList(left,whereConditionExprList);
            fillWhereConditionExprList(right,whereConditionExprList);
        }
    }
    protected void parseSQLInListExpr(Set<String> dbTbShardColumns, Map<String, List<Object>> sqlInListConditionMap, SQLInListExpr conditionItemExpr) {
        String columnName= getColumnName(conditionItemExpr.getExpr());
        if(dbTbShardColumns.contains(columnName)){
            List<SQLExpr> targetList = conditionItemExpr.getTargetList();
            for (SQLExpr sqlExpr : targetList) {
                List<Object> valueList = sqlInListConditionMap.get(columnName);
                Object shardColumnValue=sqlExpr.toString();
                if(valueList==null){
                    valueList=new ArrayList<Object>();
                    sqlInListConditionMap.put(columnName,valueList);
                }
                if(isJdbcPlaceHolder(sqlExpr)){
                    DragonPrepareStatement.ParamSetting paramSetting = getParamSetting(++currentParamterIndex);
                     shardColumnValue= paramSetting.values[0];
                }
                valueList.add(shardColumnValue);
            }
        }
    }

    protected void parseBinaryConditionExpr(Set<String> dbTbShardColumns, Map<String, Object> binaryShardConditionMap, SQLBinaryOpExpr conditionItemExpr) {
        SQLExpr valueExpr = conditionItemExpr.getRight();
        if(isJdbcPlaceHolder(valueExpr)){
            SQLBinaryOperator operator = conditionItemExpr.getOperator();
            SQLExpr shardColumnExpr = conditionItemExpr.getLeft();
            String columnName=getColumnName(shardColumnExpr);
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

    private String getColumnName(SQLExpr shardColumnExpr) {
        String columnName=null;
        if(shardColumnExpr instanceof SQLIdentifierExpr){
            columnName=((SQLIdentifierExpr) shardColumnExpr).getName();
        }
        if(shardColumnExpr instanceof SQLPropertyExpr){
            columnName=((SQLPropertyExpr) shardColumnExpr).getSimpleName();
        }
        return columnName;
    }

    protected void addRouteInfo(LogicTable logicTable, Map<String, Object> binaryShardConditionMap) {
        String routeDBIndex = logicTable.getRouteDBIndex(binaryShardConditionMap);
        String routeTBIndex = logicTable.getRouteTBIndex(binaryShardConditionMap);
        Map<String, SqlRouteInfo> dbRouteMap = context.getSqlRouteMap().get(routeDBIndex);
        if(dbRouteMap==null){
            dbRouteMap=new HashMap<String, SqlRouteInfo>();
            context.getSqlRouteMap().put(routeDBIndex,dbRouteMap);
        }
        SqlRouteInfo tbSqlRouteInfo = dbRouteMap.get(routeTBIndex);
        if(tbSqlRouteInfo==null){
            tbSqlRouteInfo=new SqlRouteInfo(routeDBIndex,routeTBIndex);
        }
        dbRouteMap.put(routeTBIndex,tbSqlRouteInfo);
    }

    protected void makeRouteMap( Map<String, Object> binaryShardConditionMap, Map<String, List<Object>> sqlInListConditionMap) {
        if(parsedLogicTableList.size()==1){
            LogicTable logicTable = parsedLogicTableList.get(0);
            //where partition=xxx的情况
            if (MapUtils.isNotEmpty(binaryShardConditionMap) && MapUtils.isEmpty(sqlInListConditionMap)) {
                addRouteInfo(logicTable, binaryShardConditionMap);
            }
            //where partition1=xxx and partition2 in(x,x,x)的情况
            if (MapUtils.isNotEmpty(sqlInListConditionMap)) {
                for (Map.Entry<String, List<Object>> entry : sqlInListConditionMap.entrySet()) {
                    String shardColumn = entry.getKey();
                    List<Object> valueList = entry.getValue();
                    for (Object value : valueList) {
                        HashMap<String, Object> routeConditionMap = new HashMap<String, Object>();
                        routeConditionMap.put(shardColumn, value);
                        if (MapUtils.isNotEmpty(binaryShardConditionMap)) {
                            routeConditionMap.putAll(binaryShardConditionMap);
                        }
                        addRouteInfo(logicTable, routeConditionMap);
                    }
                }
            }
        }

    }
    protected void fillRouteParamsMap(List<SQLExpr> whereConditionList, Map<String, Object> binaryShardConditionMap, Map<String, List<Object>> sqlInListConditionMap) {
        if(parsedLogicTableList.size()==1){
            Set<String> dbTbShardColumns = parsedLogicTableList.get(0).getDbTbShardColumns();
            for (SQLExpr conditionItemExpr : whereConditionList) {
                if (conditionItemExpr instanceof SQLBinaryOpExpr) {
                    parseBinaryConditionExpr(dbTbShardColumns, binaryShardConditionMap, (SQLBinaryOpExpr) conditionItemExpr);
                }
                if (conditionItemExpr instanceof SQLInListExpr) {
                    parseSQLInListExpr(dbTbShardColumns, sqlInListConditionMap, (SQLInListExpr) conditionItemExpr);
                }
                if (conditionItemExpr instanceof SQLBetweenExpr) {
//                conditionItemExpr.
                }

                if (conditionItemExpr instanceof SQLInSubQueryExpr) {

                }
            }
        }

    }
    /**生成更新(U)、删除(D)语句的真实sql*/
    protected void makeupRealSql() {
        //不能直接使用originSql，因为Mysql Select会对orderBy limit部分做修改
        String sql = sqlAst.toString();
        for (Map<String, SqlRouteInfo> dbRouteMap :  context.getSqlRouteMap().values()) {
            for (SqlRouteInfo tbSqlRouteInfo : dbRouteMap.values()) {
                for (LogicTable logicTable : parsedLogicTableList) {
                    String newSql = sql.replaceAll(logicTable.getLogicName(), tbSqlRouteInfo.getTableName());
                    tbSqlRouteInfo.setSql(newSql);
                    if (isPrepare) {
                        tbSqlRouteInfo.getParameters().putAll(originParameters);
                    }
                }
            }
        }
    }
}
