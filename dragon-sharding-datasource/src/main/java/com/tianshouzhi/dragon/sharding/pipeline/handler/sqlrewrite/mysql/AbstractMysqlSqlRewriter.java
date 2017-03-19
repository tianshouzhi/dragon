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

    /**
     * 解析可作为路由条件的where条件，只支持：id=？ 或者 id in(?,?,?)
     * 其他条件如> 、>=、 <、<=、betwwen and、!= 、not in、like、not like的条件，不会返回
     * @param where
     * @return
     */
    protected List<SQLExpr> parseRouteConditionList(SQLExpr where){
        List<SQLExpr> whereConditionList=new ArrayList<SQLExpr>();
        fillWhereConditionExprList(where,whereConditionList);
        return whereConditionList;
    }


    //解析结果
    private static void fillWhereConditionExprList(SQLExpr where, List<SQLExpr> whereConditionExprList){
        if(where instanceof SQLIdentifierExpr//直接列名的情况
                ||where instanceof SQLPropertyExpr){//表名.列名的情况
            SQLExpr parent = (SQLExpr) where.getParent();
            if(!whereConditionExprList.contains(parent)){
                whereConditionExprList.add(parent);
            }
            return;
        }
        if(where instanceof SQLInListExpr
                || where instanceof SQLBetweenExpr){
            whereConditionExprList.add(where);
            return;
        }

        //二元操作符放在最后解析
        if(where instanceof SQLBinaryOpExpr){
            SQLExpr left = ((SQLBinaryOpExpr) where).getLeft();
            SQLExpr right = ((SQLBinaryOpExpr) where).getRight();

            fillWhereConditionExprList(left,whereConditionExprList);
            fillWhereConditionExprList(right,whereConditionExprList);
        }
    }


    /**
     * 二元运算符条件解析 只有=号可作为分区条件，只会把currentParamterIndex增加，所有二元操作符：SQLBinaryOperator
     * @param sqlRouteParams
     * @param conditionItemExpr
     */
    private void parseBinaryRouteConditionExpr(SqlRouteParams sqlRouteParams, SQLBinaryOpExpr conditionItemExpr) {
            SQLExpr valueExpr = conditionItemExpr.getRight();
            if(valueExpr instanceof SQLIdentifierExpr || valueExpr instanceof SQLPropertyExpr){
                //处理a.id=b.id情况，这种条件不能路由路由条件，且currentParamterIndex也不需要改变
                return;
            }
            SQLBinaryOperator operator = conditionItemExpr.getOperator();
            SQLExpr shardColumnExpr = conditionItemExpr.getLeft();
            LogicTable logicTable= getLogicTable(shardColumnExpr);
            String columnName=DragonDruidASTUtil.getColumnName(shardColumnExpr);

        /*if(!(SQLBinaryOperator.NotEqual==operator
                    || SQLBinaryOperator.GreaterThan==operator
                    || SQLBinaryOperator.GreaterThanOrEqual==operator
                    ||SQLBinaryOperator.NotGreaterThan==operator

                    ||SQLBinaryOperator.LessThan==operator
                    ||SQLBinaryOperator.LessThanOrEqual==operator
                    ||SQLBinaryOperator.NotLessThan==operator

                    ||SQLBinaryOperator.LessThanOrEqualOrGreaterThan==operator

                    ||SQLBinaryOperator.Like==operator
                    ||SQLBinaryOperator.NotLike==operator
                    ||SQLBinaryOperator.NotRLike==operator

                    ||SQLBinaryOperator.Is==operator  //一般与null 连用 is null  is not null
                    ||SQLBinaryOperator.IsNot==operator)
                    ){
                throw new RuntimeException("unsupported binary operator :"+operator+" in sql :"+originSql);
            }*/
            //以上二元操作符，虽然不能作为路由条件，但是有可能有占位符，所以将currentParamterIndex+1；
            if(isJdbcPlaceHolder(valueExpr)) {
               ++currentParamterIndex;
            }
             //只将=号作为路由条件，其他二进制操作符不可作为路由条件
             if(SQLBinaryOperator.Equality==operator){
                 DragonPrepareStatement.ParamSetting paramSetting = getParamSetting(currentParamterIndex);
                 if(logicTable.getDbTbShardColumns().contains(columnName)){
                     Object shardColumnValue=valueExpr.toString(); //// TODO: 2017/3/16 支持子查询
                     if(isJdbcPlaceHolder(valueExpr)){
                         shardColumnValue=  paramSetting.values[0];
                     }
                     sqlRouteParams.putBinaryRouteParams(logicTable,columnName,shardColumnValue);
                 }
            }
    }
    private void parseSQLInRouteListExpr(SqlRouteParams sqlRouteParams, SQLInListExpr conditionItemExpr) {
        // not in 不支持作为路由条件
        if(conditionItemExpr.isNot()){
            return;
        }
        LogicTable logicTable= getLogicTable(conditionItemExpr.getExpr());
        String columnName= DragonDruidASTUtil.getColumnName(conditionItemExpr.getExpr());
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


    //根据主维度表生成路由规则
    private void addRouteInfo(LogicTable primaryLogicTable ,Map<String, Object> binaryShardConditionMap) {
        String realDBName = primaryLogicTable.getRealDBName(binaryShardConditionMap);
        String primaryTBName = primaryLogicTable.getRealTBName(binaryShardConditionMap);
        Map<String, Map<String, SqlRouteInfo>> sqlRouteMap = context.getSqlRouteMap();
        if(sqlRouteMap.containsKey(realDBName)){//这个分库的分表sql已经有了，主要用于处理id in (?,?,?)多个值路由到同一个real db的情况，只需要添加一次即可
            Map<String, SqlRouteInfo> tableRouteMap = sqlRouteMap.get(realDBName);
            if(tableRouteMap !=null&&tableRouteMap.containsKey(primaryTBName)){
                return ;
            }
        }
        Map<String, SqlRouteInfo> dbRouteMap = sqlRouteMap.get(realDBName);
        if(dbRouteMap==null){
            dbRouteMap=new HashMap<String, SqlRouteInfo>();
            sqlRouteMap.put(realDBName,dbRouteMap);
        }
//        if(StringUtils.isNoneBlank(realDBName,primaryTBName)){
        SqlRouteInfo tbSqlRouteInfo = dbRouteMap.get(primaryTBName);
        if (tbSqlRouteInfo == null) {
            tbSqlRouteInfo = new SqlRouteInfo(primaryLogicTable, realDBName, primaryTBName);
        }
        dbRouteMap.put(primaryTBName, tbSqlRouteInfo);
//        }

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

        //没有路由参数，表示需要将sql分发到所有表，构造路由到所有分库的参数
        if(MapUtils.isEmpty(context.getSqlRouteMap())){
            makeRouteAllParamsMap();
        }
    }

    /**
     * whereConditionList中包含了所有的条件，需要过滤出id = ？、in (?，?，?)类似这两种类型作为路由参数
     * 对于其他的条件，例如like ，> ，not in (？？？)等，只需要根据实际情况，确定是否将currentParamterIndex++即可
     * @param whereConditionList
     * @param sqlRouteParams
     */
    protected void fillSqlRouteParams(List<SQLExpr> whereConditionList, SqlRouteParams sqlRouteParams) {
//            Set<String> dbTbShardColumns = parsedLogicTableList.get(0).getDbTbShardColumns();
            for (SQLExpr conditionItemExpr : whereConditionList) {
                //对所有的二元操作符进行处理，参见SQLBinaryOperator枚举类定义的二元操作符
                if (conditionItemExpr instanceof SQLBinaryOpExpr) {
                    parseBinaryRouteConditionExpr(sqlRouteParams, (SQLBinaryOpExpr) conditionItemExpr);
                    continue;
                }
                //对 in (?,?,?)和not in(?,?,?)进行处理
                if (conditionItemExpr instanceof SQLInListExpr) {
                    parseSQLInRouteListExpr(sqlRouteParams, (SQLInListExpr) conditionItemExpr);
                    continue;
                }

                //对between...and 进行处理，肯定不能作为路由条件，判断是否将currentParamterIndex++即可
                if(conditionItemExpr instanceof SQLBetweenExpr){
                     //column名
//                    SQLExpr columnExpr = ((SQLBetweenExpr) conditionItemExpr).getTestExpr();
                    //分别表示开始，结束的值
                    SQLExpr beginExpr = ((SQLBetweenExpr) conditionItemExpr).getBeginExpr();
                    SQLExpr endExpr = ((SQLBetweenExpr) conditionItemExpr).getEndExpr();
                    if(beginExpr instanceof SQLVariantRefExpr){
                        currentParamterIndex++;
                    }
                    if(endExpr instanceof SQLVariantRefExpr){
                        currentParamterIndex++;
                    }
                    return;
                }

                //没有考虑到其他的条件操作符类型
                throw new UnsupportedOperationException("unsupport where condition :"+conditionItemExpr+" ,in sql:"+originSql);

            }
    }
    /**生成更新(U)、删除(D)，查询语句的真实sql*/
    protected void makeupSqlRouteInfoSqls() {
        Map<String, Map<String, SqlRouteInfo>> sqlRouteMap = context.getSqlRouteMap();
        //根据路由表进行重写sql
        for (Map<String, SqlRouteInfo> dbRouteMap :   sqlRouteMap.values()) {
            for (SqlRouteInfo tbSqlRouteInfo : dbRouteMap.values()) {
                /**主维度表的真实表名*/
                String primaryRealTBName = tbSqlRouteInfo.getPrimaryRealTBName();
                Long primaryTBIndex = tbSqlRouteInfo.getPrimaryLogicTable().parseRealTBIndex(primaryRealTBName);
                //修改AST中每一个逻辑表名为真实表名
                for (SQLIdentifierExpr sqlIdentifierExpr : sqlExprTableSourceList) {
                    String tableName = sqlIdentifierExpr.getSimpleName();
                    sqlIdentifierExpr.putAttribute("originName",tableName);
                    sqlIdentifierExpr.setName(context.getLogicTable(tableName).format(primaryTBIndex));
                }
                //不能直接使用originSql，因为Mysql Select需要对orderBy limit部分做修改
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

    private void makeRouteAllParamsMap() {
        Map<String, Map<String, SqlRouteInfo>> sqlRouteMap=new HashMap<String, Map<String, SqlRouteInfo>>();
        for (LogicTable logicTable : parsedLogicTableList) { //check每个逻辑表都应该配置了真实库与表的映射关系
            Map<String, List<String>> realDBTBMap = logicTable.getRealDBTBMap();
            if(MapUtils.isEmpty(realDBTBMap)){
                throw new RuntimeException("logic table '"+logicTable.getLogicTableName()+"' don't config real db and tb Map");
            }
        }
        LogicTable primaryLogicTable = parsedLogicTableList.get(0);//因为没有分区条件，随机选择一个表作为主维度表，这里选择第一个

        Map<String, List<String>> realDBTBMap = primaryLogicTable.getRealDBTBMap();

        for (Map.Entry<String, List<String>> realDBTBListEntry : realDBTBMap.entrySet()) {
            String realDBName = realDBTBListEntry.getKey();
            List<String> realTBNameList = realDBTBListEntry.getValue();
            Map<String, SqlRouteInfo> realTbRouteInfoMap=new HashMap<String, SqlRouteInfo>();
            for (String realTBName : realTBNameList) {
                SqlRouteInfo routeInfo=new SqlRouteInfo(primaryLogicTable,realDBName, realTBName);
                realTbRouteInfoMap.put(realTBName,routeInfo);
            }
            sqlRouteMap.put(realDBName,realTbRouteInfoMap);
        }
        context.getSqlRouteMap().putAll(sqlRouteMap);
    }
}
