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
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRouteParams;
import com.tianshouzhi.dragon.sharding.route.LogicTable;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

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
    protected Map<String,String> aliasTableNameMap=new HashMap<String, String>();
    private List<SQLIdentifierExpr> sqlExprTableSourceList=new ArrayList<SQLIdentifierExpr>();

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
            String alias = tableSource.getAlias();
            if(StringUtils.isBlank(alias)){
                alias=((SQLExprTableSource) tableSource).getExpr().toString();
            }
            sqlExprTableSourceList.add((SQLIdentifierExpr) ((SQLExprTableSource) tableSource).getExpr());
            aliasTableNameMap.put(alias,logicTableName);
            return;
        }
        if(tableSource instanceof SQLJoinTableSource){
            SQLTableSource left = ((SQLJoinTableSource) tableSource).getLeft();
            SQLTableSource right = ((SQLJoinTableSource) tableSource).getRight();
            if(!(left instanceof SQLExprTableSource)||!(right instanceof SQLExprTableSource)){
                //join 查询的两个表，不能是其他类型的表
                throw new UnsupportedOperationException("join query only support simple table!");
            }
            sqlExprTableSourceList.add((SQLIdentifierExpr) ((SQLExprTableSource) left).getExpr());
            sqlExprTableSourceList.add((SQLIdentifierExpr) ((SQLExprTableSource) right).getExpr());

            String leftLogicTableName = ((SQLExprTableSource) left).getExpr().toString();
            LogicTable leftLogicTable = context.getLogicTable(leftLogicTableName);
            String leftAlias=left.getAlias();
            String rightLogicTableName = ((SQLExprTableSource) right).getExpr().toString();
            LogicTable rightLogicTable = context.getLogicTable(rightLogicTableName);
            String rightAlias=right.getAlias();
            parsedLogicTableList.add(leftLogicTable);
            parsedLogicTableList.add(rightLogicTable);
            if(StringUtils.isNoneBlank(leftAlias)){
                aliasTableNameMap.put(leftAlias,leftLogicTableName);
            }
            if(StringUtils.isNoneBlank(leftAlias)){
                aliasTableNameMap.put(rightAlias,rightLogicTableName);
            }
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
    protected void parseSQLInListExpr(SqlRouteParams sqlRouteParams, SQLInListExpr conditionItemExpr) {
        LogicTable logicTable= getLogicTable(conditionItemExpr.getExpr());
        String columnName= getColumnName(conditionItemExpr.getExpr());
        List<Object> valueList=new ArrayList<Object>();
        if(logicTable.getDbTbShardColumns().contains(columnName)){
            List<SQLExpr> targetList = conditionItemExpr.getTargetList();
            for (SQLExpr sqlExpr : targetList) {
                Object shardColumnValue=sqlExpr.toString();
                if(isJdbcPlaceHolder(sqlExpr)){
                    DragonPrepareStatement.ParamSetting paramSetting = getParamSetting(++currentParamterIndex);
                     shardColumnValue= paramSetting.values[0];
                }
                valueList.add(shardColumnValue);
            }
            sqlRouteParams.putInListRouteParams(logicTable,columnName,valueList);
        }
    }

    protected void parseBinaryConditionExpr(SqlRouteParams sqlRouteParams, SQLBinaryOpExpr conditionItemExpr) {
        SQLExpr valueExpr = conditionItemExpr.getRight();
        if(isJdbcPlaceHolder(valueExpr)){
            SQLBinaryOperator operator = conditionItemExpr.getOperator();
            SQLExpr shardColumnExpr = conditionItemExpr.getLeft();
            LogicTable logicTable= getLogicTable(shardColumnExpr);
            String columnName=getColumnName(shardColumnExpr);
            DragonPrepareStatement.ParamSetting paramSetting = getParamSetting(++currentParamterIndex);
            if(logicTable.getDbTbShardColumns().contains(columnName)){
                if(SQLBinaryOperator.Equality==operator){
                    Object shardColumnValue=  paramSetting.values[0];
                    sqlRouteParams.putBinaryRouteParams(logicTable,columnName,shardColumnValue);
                }else{
                    throw new RuntimeException("二元操作符的分区条件，只支持=号!!!");
                }
            }
        }
    }

    private LogicTable getLogicTable(SQLExpr shardColumnExpr) {
        if(parsedLogicTableList.size()==1){
            return parsedLogicTableList.get(0);
        }

        //// TODO: 2017/3/13 只根据一个列名，怎么判断其属于哪个表
        if(shardColumnExpr instanceof SQLIdentifierExpr){//直接使用列名，则取第一个没有别名的tablesource 返回，这里可能存在问题
            String shardColumnName=((SQLIdentifierExpr) shardColumnExpr).getName();

//            return context.getLogicTable(shardColumnName);
        }
        if(shardColumnExpr instanceof SQLPropertyExpr){//表名.列名的情况
            String logicTableName = ((SQLPropertyExpr) shardColumnExpr).getOwner().toString();
            LogicTable logicTable = context.getLogicTable(logicTableName);
            if(logicTable==null){//表名可能使用的是别名
                logicTableName=aliasTableNameMap.get(logicTableName);
                logicTable = context.getLogicTable(logicTableName);
            }
            return logicTable;
        }
        throw new RuntimeException("can't decide shardColumnExpr:"+shardColumnExpr+" belong to which logic table");
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
    //根据主维度表生成路由规则
    private void addRouteInfo(LogicTable primaryLogicTable ,Map<String, Object> binaryShardConditionMap) {
        Long primaryTBIndex = primaryLogicTable.getRealTBIndex(binaryShardConditionMap);
        String realDBName = primaryLogicTable.getRealDBName(binaryShardConditionMap);
        String primaryTBName = primaryLogicTable.getRealTBName(binaryShardConditionMap);
        Map<String, SqlRouteInfo> dbRouteMap = context.getSqlRouteMap().get(realDBName);
        if(dbRouteMap==null){
            dbRouteMap=new HashMap<String, SqlRouteInfo>();
            context.getSqlRouteMap().put(realDBName,dbRouteMap);
        }
        if(StringUtils.isNoneBlank(realDBName,primaryTBName)){
            SqlRouteInfo tbSqlRouteInfo = dbRouteMap.get(primaryTBName);
            if(tbSqlRouteInfo==null){
                tbSqlRouteInfo=new SqlRouteInfo(realDBName, primaryTBIndex,primaryTBName);
            }
            dbRouteMap.put(primaryTBName,tbSqlRouteInfo);
        }

    }

    protected void makeRouteMap(SqlRouteParams sqlRouteParams) {
        //如果sql中只包含一个表，则可以执行
        //主维度表
        LogicTable primaryLogicTable = sqlRouteParams.getPrimaryLogicTable();
        Map<String, Object> binaryRouteParamsMap = sqlRouteParams.getBinaryRouteParamsMap();
        Map<String, List<Object>> sqlInListParamsMap = sqlRouteParams.getSqlInListRouteParamsMap();
        //where partition=xxx的情况
        if (MapUtils.isNotEmpty(binaryRouteParamsMap) && MapUtils.isEmpty(sqlInListParamsMap)) {
            addRouteInfo(primaryLogicTable,binaryRouteParamsMap);
        }
        //where id in(x,x,x)的情况，支持与binary条件联合确定路由规则
        if (MapUtils.isNotEmpty(sqlInListParamsMap)) {
            for (Map.Entry<String, List<Object>> entry : sqlInListParamsMap.entrySet()) {
                String shardColumn = entry.getKey();
                List<Object> valueList = entry.getValue();
                for (Object value : valueList) {
                    HashMap<String, Object> routeConditionMap = new HashMap<String, Object>();
                    routeConditionMap.put(shardColumn, value);
                    if (MapUtils.isNotEmpty(binaryRouteParamsMap)) {
                        routeConditionMap.putAll(binaryRouteParamsMap);
                    }
                    addRouteInfo(primaryLogicTable,routeConditionMap);
                }
            }
        }
    }
    protected void fillSqlRouteParams(List<SQLExpr> whereConditionList, SqlRouteParams sqlRouteParams) {
//            Set<String> dbTbShardColumns = parsedLogicTableList.get(0).getDbTbShardColumns();
            for (SQLExpr conditionItemExpr : whereConditionList) {
                if (conditionItemExpr instanceof SQLBinaryOpExpr) {
                    parseBinaryConditionExpr(sqlRouteParams, (SQLBinaryOpExpr) conditionItemExpr);
                    continue;
                }
                if (conditionItemExpr instanceof SQLInListExpr) {
                    parseSQLInListExpr(sqlRouteParams, (SQLInListExpr) conditionItemExpr);
                    continue;
                }
                if (conditionItemExpr instanceof SQLBetweenExpr) {
//                conditionItemExpr.
                }

                if (conditionItemExpr instanceof SQLInSubQueryExpr) {

                }
            }


    }
    /**生成更新(U)、删除(D)语句的真实sql*/
    protected void makeupSqlRouteInfoSqls() {
        //不能直接使用originSql，因为Mysql Select需要对orderBy limit部分做修改
//        String sql = sqlAst.toString();
        for (Map<String, SqlRouteInfo> dbRouteMap :  context.getSqlRouteMap().values()) {
            for (SqlRouteInfo tbSqlRouteInfo : dbRouteMap.values()) {
                for (SQLIdentifierExpr sqlIdentifierExpr : sqlExprTableSourceList) {
                    String tableName = sqlIdentifierExpr.getSimpleName();
                    sqlIdentifierExpr.putAttribute("originName",tableName);
                    sqlIdentifierExpr.setName(context.getLogicTable(tableName).format(tbSqlRouteInfo.getPrimaryTBIndex()));
                }
                String newSql = sqlAst.toString();
                tbSqlRouteInfo.setSql(newSql);
                if (isPrepare) {
                    tbSqlRouteInfo.getParameters().putAll(originParameters);
                }
                for (SQLIdentifierExpr sqlIdentifierExpr : sqlExprTableSourceList) {
                    String originName = (String) sqlIdentifierExpr.getAttribute("originName");
                    if(originName!=null){
                        sqlIdentifierExpr.setName(originName);
                    }
                }
            }
        }
    }
}
