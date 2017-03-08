package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.sqlrewriter.mysql;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.tianshouzhi.dragon.common.jdbc.statement.DragonPrepareStatement;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingPrepareStatement;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingStatement;
import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRouteInfo;
import com.tianshouzhi.dragon.sharding.route.LogicTable;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 <b>Single-Table Syntax</b>

 DELETE [LOW_PRIORITY] [QUICK] [IGNORE] FROM tbl_name
 [PARTITION (partition_name,...)]
 [WHERE where_condition]
 [ORDER BY ...]
 [LIMIT row_count]

 <b>Multiple-Table Syntax 不支持</b>

 DELETE [LOW_PRIORITY] [QUICK] [IGNORE]
 tbl_name[.*] [, tbl_name[.*]] ...
 FROM table_references
 [WHERE where_condition]
Or:
 DELETE [LOW_PRIORITY] [QUICK] [IGNORE]
 FROM tbl_name[.*] [, tbl_name[.*]] ...
 USING table_references
 [WHERE where_condition]
 </pre>
 */
public class MysqlDeleteStatementRewriter extends AbstractMysqlSqlRewriter {
    @Override
    public Map<String, Map<String, SqlRouteInfo>> doRewrite(HandlerContext context) throws SQLException {
        MySqlDeleteStatement deleteAst= (MySqlDeleteStatement) context.getParsedSqlStatement();
        SQLExpr where = deleteAst.getWhere();
        DragonShardingStatement shardingStatement = context.getDragonShardingStatement();

        checkNotSupport(deleteAst, where ,shardingStatement.getSql());

        SQLTableSource tableSource= deleteAst.getTableSource();

        StringBuilder deleteSqlPrefix=new StringBuilder();
        deleteSqlPrefix.append("DELETE ");
        if(deleteAst.isLowPriority()){
            deleteSqlPrefix.append("LOW_PRIORITY ");
        }
        if(deleteAst.isQuick()){
            deleteSqlPrefix.append("QUICK ");
        }
        if(deleteAst.isIgnore()){
            deleteSqlPrefix.append("IGNORE ");
        }
        deleteSqlPrefix.append("FROM ");

        String logicTableName = ((SQLExprTableSource) tableSource).getExpr().toString();
        LogicTable logicTable = context.getLogicTable(logicTableName);
        Set<String> dbTbShardColumns = logicTable.getDbTbShardColumns();

        DragonShardingStatement dragonShardingStatement = shardingStatement;

        Map<String,Map<String,SqlRouteInfo>> dbIndexSplitMap=new HashMap<String, Map<String, SqlRouteInfo>>();
        //获得where中包含的所有条件
        List<SQLExpr> whereConditionList = parseWhereCondition(where);
        //分区字段参数列表
        HashMap<String,String> params=new HashMap<String, String>();
        int paramIndex=0;
        for (SQLExpr sqlExpr : whereConditionList) {
            if(sqlExpr instanceof SQLBinaryOpExpr){
                SQLBinaryOperator operator = ((SQLBinaryOpExpr) sqlExpr).getOperator();
                String shardColumn = ((SQLBinaryOpExpr) sqlExpr).getLeft().toString();
                if(dbTbShardColumns.contains(shardColumn)){
                    if(SQLBinaryOperator.Equality==operator){//二元操作分区字段，只支持=号
                        throw new RuntimeException("binary shard condition only support '=' operator!!!,sql:"+shardingStatement.getSql());
                    }
                    String shardValue = ((SQLBinaryOpExpr) sqlExpr).getRight().toString();
                    if("?".equals(shardValue)&& dragonShardingStatement instanceof DragonShardingPrepareStatement){
                        DragonShardingPrepareStatement shardingPrepareStatement = (DragonShardingPrepareStatement) dragonShardingStatement;
                        Map<Integer, DragonPrepareStatement.ParamSetting> parameters = shardingPrepareStatement.getParameters();
                        shardValue=parameters.get(++paramIndex).values[0].toString();
                    }
                    params.put(shardColumn,shardValue);
                }
            }
            if(where instanceof SQLInListExpr){//where id in (?,?,?,?)
                String shardColumn = ((SQLInListExpr) where).getExpr().toString();
                if(dbTbShardColumns.contains(shardColumn)){//如果这个字段是分区字段
                    List<SQLExpr> targetList = ((SQLInListExpr) where).getTargetList();
                    for (int i = 0; i < targetList.size(); i++) {
                        SQLExpr shardColumnValueExpr=targetList.get(i);
                        String shardColumnValue=shardColumnValueExpr.toString();
                        if(shardColumnValueExpr instanceof SQLVariantRefExpr//?
                                && dragonShardingStatement instanceof DragonShardingPrepareStatement){//?号
                            DragonShardingPrepareStatement shardingPrepareStatement = (DragonShardingPrepareStatement) dragonShardingStatement;
                            Map<Integer, DragonPrepareStatement.ParamSetting> parameters = shardingPrepareStatement.getParameters();
                            shardColumnValue=parameters.get(++paramIndex).values[0].toString();
                        }
                        HashMap<String, Object> shardColumnValuesMap = new HashMap<String, Object>();
                        shardColumnValuesMap.put(shardColumn,shardColumnValue);
                        String routeDBIndex = logicTable.getRouteDBIndex(shardColumnValuesMap);
                        String routeTBIndex = logicTable.getRouteTBIndex(shardColumnValuesMap);
                        if(StringUtils.isAnyBlank(routeDBIndex,routeTBIndex)){
                            throw new SQLException();//删除语句中没有包含分区字段的值
                        }
                        Map<String, SqlRouteInfo> tbIndexSpitMap = dbIndexSplitMap.get(routeDBIndex);
                        if(tbIndexSpitMap==null){
                            tbIndexSpitMap=new HashMap<String, SqlRouteInfo>();
                        }
                        if(!tbIndexSpitMap.containsKey(routeTBIndex)){
                            tbIndexSpitMap.put(routeTBIndex,new SqlRouteInfo(routeDBIndex,routeTBIndex));
                        }
                        SqlRouteInfo sqlSplitInfo = tbIndexSpitMap.get(routeTBIndex);
                        DragonShardingPrepareStatement shardingPrepareStatement = (DragonShardingPrepareStatement) dragonShardingStatement;
                        sqlSplitInfo.addParam(shardingPrepareStatement.getParameters().get(paramIndex));
                        dbIndexSplitMap.put(routeDBIndex,tbIndexSpitMap);
                    }
                }
           /* for (Map.Entry<String, Map<String, SqlRouteInfo>> dbSqlEntry : dbIndexSplitMap.entrySet()) {
                Map<String, SqlRouteInfo> tbSqlEntryMap = dbSqlEntry.getValue();
                for (SqlRouteInfo sqlRouteInfo : tbSqlEntryMap.values()) {
                    StringBuilder sql=new StringBuilder(deleteSqlPrefix.toString());
                    sql.append(sqlRouteInfo.getTableName()+" ");
                    sql.append("WHERE ");
                    sql.append(shardColumn+" ");
                    sql.append("IN ");
                    sql.append("(");
                    Map<Integer, DragonPrepareStatement.ParamSetting> originParameters = sqlRouteInfo.getParameters();
                    for (int i = 0; i < originParameters.entrySet().size(); i++) {
                        sql.append("?");
                        if(i!=originParameters.entrySet().size()-1){
                            sql.append(",");
                        }
                    }
                    sql.append(")");
                    sqlRouteInfo.setSql(sql);
                }
              }*/

            }else{//其他的条件
                String otherCondition = sqlExpr.toString();
                int countMatches = StringUtils.countMatches(otherCondition, "?");
                paramIndex+=countMatches;
            }
        }

        return dbIndexSplitMap;
    }


    private static void checkNotSupport(MySqlDeleteStatement deleteAst, SQLExpr where,String sql) {
        SQLTableSource tableSource = deleteAst.getTableSource();
        if(!(tableSource instanceof SQLExprTableSource)){
            throw new RuntimeException("don't support Multiple-Table delete Syntax!!!sql:"+sql);
        }
        if(where==null){
            throw new RuntimeException("delete sql must contains where condition!!!sql:"+ sql);
        }
        if(where instanceof SQLInSubQueryExpr){
            throw new RuntimeException("where condition don't support sub query!!!sql:"+ sql);
        }
    }
}
