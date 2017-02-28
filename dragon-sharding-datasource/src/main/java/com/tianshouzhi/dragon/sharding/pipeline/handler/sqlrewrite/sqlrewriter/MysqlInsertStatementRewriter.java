package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.sqlrewriter;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.tianshouzhi.dragon.common.jdbc.statement.DragonPrepareStatement;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingPrepareStatement;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingStatement;
import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import com.tianshouzhi.dragon.sharding.route.LogicTable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.*;

/**
 * mysql插入语法：
 * <pre>
 * Syntax:
 <b>1)insert into values语法：</b>
 INSERT [LOW_PRIORITY | DELAYED | HIGH_PRIORITY] [IGNORE]
 [INTO] tbl_name
 [PARTITION (partition_name,...)]
 [(col_name,...)]
 {VALUES | VALUE} ({expr | DEFAULT},...),(...),...
 [ ON DUPLICATE KEY UPDATE
 col_name=expr
 [, col_name=expr] ... ]

 <b>2)insert into set 语法：</b>
 INSERT [LOW_PRIORITY | DELAYED | HIGH_PRIORITY] [IGNORE]
 [INTO] tbl_name
 [PARTITION (partition_name,...)]
 SET col_name={expr | DEFAULT}, ...
 [ ON DUPLICATE KEY UPDATE
 col_name=expr
 [, col_name=expr] ... ]

 <b>3):insert into select语法</b>
 INSERT [LOW_PRIORITY | HIGH_PRIORITY] [IGNORE]
 [INTO] tbl_name
 [PARTITION (partition_name,...)]
 [(col_name,...)]
 SELECT ...
 [ ON DUPLICATE KEY UPDATE
 col_name=expr
 [, col_name=expr] ... ]
 * </pre>
 */
public class MysqlInsertStatementRewriter {
    public void rewrite(MySqlInsertStatement sqlStatement, HandlerContext context) throws SQLException{
        String insertClause="insert into ";
        String logicTableName = sqlStatement.getTableName().getSimpleName().toString();
//        insertClause.append().append(logicTableName);

        List<SQLExpr> columns = sqlStatement.getColumns();
        // insert into table_xxx values(，，)语法不支持
        if(CollectionUtils.isEmpty(columns)){
            throw new SQLException("insert sql must contains columns");
        }

        LogicTable logicTable =context.getLogicTable(logicTableName);
        /**用户针对逻辑表配置的分区字段*/
        Set<String> configShardColumns = logicTable.getDbTbShardColumns();
        //sql中包含的分区字段出现的位置和名称的映射关系
        Map<Integer,String> shardColumnIndexNameMap=new HashMap<Integer, String>();
        //添加列名
        StringBuilder columnClause=new StringBuilder();
        columnClause.append("(");
        for(int i = 0; i < columns.size(); i++) {
            String columnName = columns.get(i).toString();
            columnClause.append(columnName);
            if(i < columns.size() - 1){
                columnClause.append(",");
            }
            if(configShardColumns.contains(columnName)){
                shardColumnIndexNameMap.put(i,columnName);
            }
        }
        //insert 语句中必须包含分区字段
        if(shardColumnIndexNameMap==null){
            throw new SQLException("insert sql("+ context.getDragonShardingStatement().getSql()+") must contains shard column!!!");
        }
        columnClause.append(")");

        if(CollectionUtils.isEmpty(context.getDragonShardingStatement().getBatchExecuteInfoList())){//非批处理
            List<SQLInsertStatement.ValuesClause> valuesList = sqlStatement.getValuesList();
            DragonShardingStatement dragonShardingStatement = context.getDragonShardingStatement();

            Map<String,Map<String,SplitTableStatmentInfo>> dbIndexSplitMap=new HashMap<String, Map<String, SplitTableStatmentInfo>>();
            for (int i = 0; i < valuesList.size(); i++) {
                SQLInsertStatement.ValuesClause valuesClause = valuesList.get(i);
                List<SQLExpr> values = valuesClause.getValues();
                //取第一个非空的分区字段值作为路由条件
                Iterator<Map.Entry<Integer, String>> iterator = shardColumnIndexNameMap.entrySet().iterator();
                Map<String,String> routeParams=new HashMap<String, String>();
                while (iterator.hasNext()){
                    Map.Entry<Integer, String> next = iterator.next();
                    Integer shardColumnIndex = next.getKey();
                    String shardColumnName = next.getValue();
                    String shardColumnValue = values.get(shardColumnIndex).toString();
                    if(dragonShardingStatement instanceof DragonShardingPrepareStatement
                            &&"?".equals(shardColumnValue)){
                        DragonShardingPrepareStatement shardingPrepareStatement= (DragonShardingPrepareStatement) dragonShardingStatement;
                        Map<Integer, DragonPrepareStatement.ParamSetting> parameters = shardingPrepareStatement.getParameters();
                        if(parameters!=null&&!parameters.isEmpty()){
                            shardColumnValue= parameters.get(i * (parameters.size()/valuesList.size()) + shardColumnIndex+1).values[0].toString();
                        }
                    }
                    if(StringUtils.isBlank(shardColumnValue)){
                        continue;
                    }
                    routeParams.put(shardColumnName,shardColumnValue);
                }

                String routeDBIndex=logicTable.getRouteDBIndex(routeParams);
                String routeTBIndex=logicTable.getRouteTBIndex(routeParams);

                if(StringUtils.isAnyBlank(routeDBIndex,routeTBIndex)){
                    throw new SQLException();//插入语句中没有包含分区字段的值
                }
                Map<String, SplitTableStatmentInfo> tbIndexSpitMap = dbIndexSplitMap.get(routeDBIndex);
                if(tbIndexSpitMap==null){
                    tbIndexSpitMap=new HashMap<String, SplitTableStatmentInfo>();
                    tbIndexSpitMap.put(routeTBIndex,new SplitTableStatmentInfo(routeTBIndex));
                }
                SplitTableStatmentInfo sqlSplitInfo = tbIndexSpitMap.get(routeTBIndex);
                if(sqlSplitInfo==null){
                    sqlSplitInfo=new SplitTableStatmentInfo(routeTBIndex);
                }
//            sqlSplitInfo.sql.append(insertClause);
                sqlSplitInfo.valuesClauseList.add(valuesClause);
//            appendValues(valuesClause.getValues(),sqlSplitInfo.sql);
                DragonShardingPrepareStatement shardingPrepareStatement= (DragonShardingPrepareStatement) dragonShardingStatement;
                Map<Integer, DragonPrepareStatement.ParamSetting> parameters = shardingPrepareStatement.getParameters();
                int paramStartIndex=parameters.size()/valuesList.size()*i+1;
                int paramEndIndex=paramStartIndex+parameters.size()/valuesList.size();
                for ( ;paramStartIndex<paramEndIndex;paramStartIndex++){
                    sqlSplitInfo.addParam(parameters.get(paramStartIndex));
                }

                tbIndexSpitMap.put(routeTBIndex,sqlSplitInfo);
                dbIndexSplitMap.put(routeDBIndex,tbIndexSpitMap);
            }
            //on duplicate key update语法
            StringBuilder duplicateKeyUpdateStr=null;
            List<SQLExpr> duplicateKeyUpdate = sqlStatement.getDuplicateKeyUpdate();
            if(duplicateKeyUpdate != null && duplicateKeyUpdate.size() > 0){
                duplicateKeyUpdateStr.append(" on duplicate key update ");
                for(int i=0; i<duplicateKeyUpdate.size(); i++){
                    SQLExpr exp = duplicateKeyUpdate.get(i);
                    if(exp != null){
                        duplicateKeyUpdateStr.append(exp.toString());
                        if(i < duplicateKeyUpdate.size() - 1)
                            duplicateKeyUpdateStr.append(",");
                    }
                }
            }

            for (Map.Entry<String, Map<String, SplitTableStatmentInfo>> dbSqlEntry : dbIndexSplitMap.entrySet()) {
                Map<String, SplitTableStatmentInfo> tbSqlEntryMap = dbSqlEntry.getValue();
                for (SplitTableStatmentInfo splitTableStatmentInfo : tbSqlEntryMap.values()) {
                    splitTableStatmentInfo.makeSql(insertClause,columnClause,duplicateKeyUpdateStr);
                }
            }
            System.out.println(dbIndexSplitMap);
        }else {//批处理

        }


    }

    private static class SplitTableStatmentInfo {
        private String tableName;

        public SplitTableStatmentInfo(String tableName) {
            this.tableName = tableName;
        }

        private List<SQLInsertStatement.ValuesClause> valuesClauseList=new ArrayList<SQLInsertStatement.ValuesClause>();
        private Map<Integer, DragonPrepareStatement.ParamSetting> parameters=new HashMap<Integer, DragonPrepareStatement.ParamSetting>();
        private StringBuilder sql;
        public void addParam(DragonPrepareStatement.ParamSetting paramSetting) {
            if(parameters==null){
                parameters=new HashMap<Integer, DragonPrepareStatement.ParamSetting>();
            }
            parameters.put(parameters.size()+1,paramSetting);
        }

        private void makeSql(String insertClause, StringBuilder columnClause, StringBuilder duplicateKeyUpdateStr) {
            sql=new StringBuilder();
            sql.append(insertClause)
                    .append(tableName)
                    .append(columnClause)
                    .append(" values ");

            if(valuesClauseList != null && valuesClauseList.size() > 1){
                for(int j=0; j<valuesClauseList.size(); j++){
                    appendValues(valuesClauseList.get(j).getValues(), sql);
                    if(j != valuesClauseList.size() - 1)
                        sql .append(",");
                }
            }else{  // 非批量 insert
                List<SQLExpr> valuse = valuesClauseList.get(0).getValues();
                appendValues(valuse, sql);
            }
            if(duplicateKeyUpdateStr!=null){
                sql.append(duplicateKeyUpdateStr);
            }
        }
        private static StringBuilder appendValues(List<SQLExpr> valuse, StringBuilder sql){
            int size = valuse.size();
            sql.append("(");
            for(int i = 0; i < size; i++) {
                if(i < size - 1){
                    sql.append(valuse.get(i).toString()).append(",");
                }else{
                    sql.append(valuse.get(i).toString());
                }
            }
            return sql.append(")");
        }
    }
}
