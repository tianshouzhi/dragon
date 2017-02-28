package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import com.tianshouzhi.dragon.common.jdbc.statement.DragonPrepareStatement;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingPrepareStatement;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingStatement;
import com.tianshouzhi.dragon.sharding.pipeline.handler.Handler;
import com.tianshouzhi.dragon.sharding.pipeline.handler.HandlerContext;
import com.tianshouzhi.dragon.sharding.route.LogicTable;
import org.apache.commons.collections.CollectionUtils;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public class SqlRewriteHandler implements Handler {
    @Override
    public void invoke(HandlerContext context) throws SQLException{
        SQLStatement sqlStatement=parseAst(context.getDragonShardingStatement().getSql());
        if(sqlStatement instanceof SQLInsertStatement){
                parseInsertInfo((SQLInsertStatement)sqlStatement,context);
        }else if(sqlStatement instanceof SQLUpdateStatement){
//            parseUpdateInfo((SQLUpdateStatement)sqlStatement);
        }else if(sqlStatement instanceof SQLDeleteStatement){
//            parseDeleteInfo((SQLDeleteStatement)sqlStatement);
        }else if(sqlStatement instanceof SQLSelectStatement){
//            parseSelectInfo((SQLSelectStatement)sqlStatement);
        }
    }

    private void parseInsertInfo(SQLInsertStatement sqlStatement, HandlerContext context) throws SQLException{
        String tableName = sqlStatement.getTableName().getSimpleName();
        LogicTable logicTable =context.getLogicTable(tableName);
        /**用户针对逻辑表配置的分区字段*/
        Set<String> configShardColumns = logicTable.getMergedShardColumns();
        List<SQLExpr> columns = sqlStatement.getColumns();
        DragonShardingStatement shardingStatement = context.getDragonShardingStatement();
        if(CollectionUtils.isEmpty(columns)){
            throw new SQLException("insert sql("+ shardingStatement.getSql()+") must contains column names!!!");
        }
        /**插入的记录中，这些分区字段出现的位置映射关系*/
        Map<Integer,String> usedShardColumnMap=new HashMap<Integer,String>();
        for (int i = 0; i < columns.size(); i++) {
            String columnName = columns.get(i).toString();
            if(configShardColumns.contains(columnName)){
                usedShardColumnMap.put(i,columnName);
            }
        }
        if(usedShardColumnMap.isEmpty()){
            throw new SQLException("insert sql("+ shardingStatement.getSql()+") must contains shard column!!!");
        }
        List<Object> batchExecuteInfoList = shardingStatement.getBatchExecuteInfoList();

        if(CollectionUtils.isEmpty(batchExecuteInfoList)){//非批处理
            List<SQLInsertStatement.ValuesClause> valuesList = sqlStatement.getValuesList();
            // 批量insert：insert into tab(id,name) values(1,'a'),(2,'b'),(3,'c');
            // 非批量 insert:insert：insert into tab(id,name) values(1,'a');
//            Map<String,List<SQLExpr>> shardColumnValues=new HashMap<String, List<SQLExpr>>();
            Map<String/*dbIndex*/,Map<String/*tbIndex*/,DragonPrepareStatement.ParamSetting>> spiltSqlMap=new HashMap<String, Map<String, DragonPrepareStatement.ParamSetting>>();
            for (int i = 0; i < valuesList.size(); i++) {
                SQLInsertStatement.ValuesClause valuesClause = valuesList.get(i);
                List<SQLExpr> values = valuesClause.getValues();
                Map<String,String> shardColumnNameValueMapping=new HashMap<String, String>();
                for (Map.Entry<Integer, String> shardColumnEntry : usedShardColumnMap.entrySet()) {
                    Integer shardColumnIndex=shardColumnEntry.getKey();
                    String shardColumnName = shardColumnEntry.getValue();
                    String shardColumnValue = values.get(shardColumnIndex).toString().trim();
                    if("?".equals(shardColumnValue)){
                        DragonShardingPrepareStatement shardingPrepareStatement= (DragonShardingPrepareStatement) shardingStatement;
//                        Map<Integer, DragonPrepareStatement.ParamSetting> parameters = shardingPrepareStatement.getParameters();
//                        shardColumnValue= parameters.get(i * (parameters.size()/valuesList.size()) + shardColumnIndex).values[0].toString();
                    }
                    shardColumnNameValueMapping.put(shardColumnName,shardColumnValue);
                }

                String realDBIndex=null;
                String realTBIndex=null;
                for (Map.Entry<String, String> shardColumnNameValueEntry : shardColumnNameValueMapping.entrySet()) {
                     String shardColumnName = shardColumnNameValueEntry.getKey();
                     String shardColumnValue = shardColumnNameValueEntry.getValue();
//                     String currentRealDBIndex=logicTable.getRealDBIndex(shardColumnName,shardColumnValue);
//                     String currentRealTBIndex=logicTable.getRealTBIndex(shardColumnName,shardColumnValue);
                    if(realDBIndex==null&&realTBIndex==null){
//                        realDBIndex=currentRealDBIndex;
//                        realTBIndex=currentRealTBIndex;
                    }else{
//                        if(currentRealTBIndex.equals(realTBIndex)||currentRealDBIndex.equals(realDBIndex)){
//                            throw new SQLException("shard columns confilicted!!!sql:"+shardingStatement.toString());
//                        }
                    }
                }
                if(realDBIndex==null||realTBIndex==null){

                }else{
                    if(spiltSqlMap.containsKey(realDBIndex)){
                        Map<String, DragonPrepareStatement.ParamSetting> tbIndexParamsMap = spiltSqlMap.get(realDBIndex);
                        if(!tbIndexParamsMap.containsKey(realTBIndex)){
                            tbIndexParamsMap=new HashMap<String, DragonPrepareStatement.ParamSetting>();
                            List<SQLInsertStatement.ValuesClause> tableValuesList=new ArrayList<SQLInsertStatement.ValuesClause>();
                            tableValuesList.add(valuesClause);
                            sqlStatement.setValuesList(tableValuesList);

//                            tbIndexParamsMap.put(realTBIndex,)
                            ;
                        }

                    }else{
                        spiltSqlMap.put(realDBIndex,new HashMap<String, DragonPrepareStatement.ParamSetting>());
                    }
                }
            }
        }else{//批处理
//            batchExecuteInfoList
        }
    }

    private SQLStatement parseAst(String sql) throws SQLException {
        SQLStatementParser sqlStatementParser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
        List<SQLStatement> sqlStatementList = sqlStatementParser.parseStatementList();
        if(sqlStatementList.size()!=1){
            throw new SQLException("only one sql supported:"+sql);
        }else{
             return sqlStatementList.get(0);
        }
    }

}
