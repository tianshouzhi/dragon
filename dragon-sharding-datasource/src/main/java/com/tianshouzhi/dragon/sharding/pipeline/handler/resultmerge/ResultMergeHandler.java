package com.tianshouzhi.dragon.sharding.pipeline.handler.resultmerge;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.tianshouzhi.dragon.common.util.SqlTypeUtil;
import com.tianshouzhi.dragon.sharding.jdbc.resultset.ColumnMetaData;
import com.tianshouzhi.dragon.sharding.jdbc.resultset.DragonResultSetMetaData;
import com.tianshouzhi.dragon.sharding.jdbc.resultset.DragonShardingResultSet;
import com.tianshouzhi.dragon.sharding.jdbc.resultset.RowRecord;
import com.tianshouzhi.dragon.sharding.pipeline.Handler;
import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.SqlRouteInfo;
import org.apache.commons.collections.CollectionUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by TIANSHOUZHI336 on 2016/12/11.
 */
public class ResultMergeHandler implements Handler {
    @Override
    public void invoke(HandlerContext context) throws Exception {

        Map<String, Map<String, SqlRouteInfo>> sqlRewriteResult = context.getSqlRouteMap();
        List<PreparedStatement> preparedStatementList = getRealPreparedStatementList(sqlRewriteResult);
        boolean query = SqlTypeUtil.isQuery(context.getDragonShardingStatement().getSql(), true);
        context.setIsQuery(query);
        if (!query) {//如果是增删改
            int totalUpdateCount = 0;
            for (PreparedStatement preparedStatement : preparedStatementList) {
                try {
                    totalUpdateCount = totalUpdateCount + preparedStatement.getUpdateCount();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            context.setTotalUpdateCount(totalUpdateCount);
        }else{//如果是查询语句
            DragonShardingResultSet shardingResultSet=new DragonShardingResultSet();
            //设置ResultSetMetaData
            DragonResultSetMetaData metaData = makeResultSetMetaData(preparedStatementList.get(0).getResultSet().getMetaData());
            shardingResultSet.setMetaData(metaData);

            //合并查询结果集
            List<RowRecord> totalRowRecords = mergeResultSets(metaData,preparedStatementList);
            if(CollectionUtils.isEmpty(totalRowRecords)){
                return ;
            }
            //处理order by和limit 以及max、min、groupBy等函数
            if(preparedStatementList.size()==1){//只有一个statement，数据库已经排序好，不需要再次排序和解析limit
                return;
            }else{
                SQLSelectStatement parsedSqlStatement = (SQLSelectStatement)context.getParsedSqlStatement();
                MySqlSelectQueryBlock selectQuery = (MySqlSelectQueryBlock) parsedSqlStatement.getSelect().getQuery();

                //处理order by
                tackleOrderBy(selectQuery.getOrderBy(), totalRowRecords);

                //处理聚合函数与group by
                tackleAggrAndGroupBy(metaData, totalRowRecords, selectQuery);

                //处理limit
                totalRowRecords=limit(selectQuery.getLimit(),context.getOffset(),context.getRowCount(),totalRowRecords);
            }

            shardingResultSet.setRowRecords(totalRowRecords);
            context.setMergedResultSet(shardingResultSet);
//
        }

    }

    private void tackleAggrAndGroupBy(DragonResultSetMetaData metaData, List<RowRecord> totalRowRecords, MySqlSelectQueryBlock selectQuery) throws SQLException {
        SQLAggregateExpr firstAggregate=null;//只取第一个聚合函数进行操作
        int aggregateColumnIndex=0;
        for (int i = 0; i < selectQuery.getSelectList().size(); i++) {
            SQLExpr expr = selectQuery.getSelectList().get(i).getExpr();
            if(expr instanceof SQLAggregateExpr){//聚合函数
                firstAggregate= (SQLAggregateExpr) expr;
                aggregateColumnIndex=i+1;
                break;
            }
        }
        if(firstAggregate!=null){
            final String methodName = firstAggregate.getMethodName();
            SQLSelectGroupByClause groupBy = selectQuery.getGroupBy();
            Map<Object/**group Key*/,List<RowRecord>> groupByMap=new HashMap<Object, List<RowRecord>>();
            if(groupBy==null){//如果没有group by，则所有都是同一组
                groupByMap.put("*",totalRowRecords);
            }else{
                String groupByColumnLabel = groupBy.getItems().get(0).toString();
                for (RowRecord rowRecord : totalRowRecords) {
                    Object groupKey = rowRecord.getValue(groupByColumnLabel);
                    List<RowRecord> rowRecords = groupByMap.get(groupKey);
                    if(rowRecords==null){
                        rowRecords=new ArrayList<RowRecord>();
                        groupByMap.put(groupKey,rowRecords);
                    }
                    rowRecords.add(rowRecord);
                }
            }

            if ("COUNT".equals(methodName)) {
                List<RowRecord> returnRecordList = new ArrayList<RowRecord>();
                for (Map.Entry<Object, List<RowRecord>> groupByEntry : groupByMap.entrySet()) {
                    List<RowRecord> groupByList = groupByEntry.getValue();
                    RowRecord returnRecord = groupByList.get(0);
                    long count = 0;
                    for (RowRecord rowRecord : groupByList) {
                        Long value = (Long) rowRecord.getValue(aggregateColumnIndex);
                        count += value;
                    }
                    returnRecord.putColumnValue(aggregateColumnIndex, metaData.getColumnLabel(aggregateColumnIndex-1), count);
                    returnRecordList.add(returnRecord);
                }
                totalRowRecords.clear();
                totalRowRecords.addAll(returnRecordList);
            }
            if ("MAX".equals(methodName) || "MIN".equals(methodName)) {
                List<RowRecord> groupByResult=new ArrayList<RowRecord>();
                for (List<RowRecord> groupByRecords : groupByMap.values()) {
                    RowRecord returnRecord=groupByRecords.get(0);
                    for (int i = 1; i < groupByRecords.size(); i++) {
                        //String、number、Date类型都实现了Comparable接口，可以直接比较，其他类型的比较不支持
                        Comparable v1= (Comparable) returnRecord.getValue(aggregateColumnIndex);
                        RowRecord iteratorRecord = groupByRecords.get(i);
                        Comparable v2= (Comparable) iteratorRecord.getValue(aggregateColumnIndex);
                        int result = v1.compareTo(v2);
                        if("MAX".equals(methodName)){
                            if(result<0){returnRecord=iteratorRecord;}
                        }else{
                            if(result>0){returnRecord=iteratorRecord;}
                        }
                    }
                    groupByResult.add(returnRecord);
                }
                totalRowRecords.clear();
                totalRowRecords.addAll(groupByResult);
            }
        }

    }

    private List<RowRecord> limit(MySqlSelectQueryBlock.Limit limit, int offset, int rowcount, List<RowRecord> totalRowRecords) {
        if(limit==null){
           return totalRowRecords;
        }
        final int start=offset;
        final int end=Math.min(offset+rowcount,totalRowRecords.size());
        List<RowRecord> subList = totalRowRecords.subList(start, end);
        return subList;
    }

    private void tackleOrderBy(SQLOrderBy orderBy, List<RowRecord> totalRowRecords) {
        if(orderBy==null){
            return ;
        }
        final List<SQLSelectOrderByItem> items = orderBy.getItems();

        Comparator<RowRecord> comparator = new Comparator<RowRecord>() {
            @Override
            public int compare(RowRecord o1, RowRecord o2) {
                int result=0;
                for (SQLSelectOrderByItem item : items) {
                    boolean asc= SQLOrderingSpecification.ASC==item.getType();
                    String columnLabel = item.getExpr().toString();
                    Object o1_value = o1.getValue(columnLabel);
                    Object o2_value = o2.getValue(columnLabel);
                    if(o1_value instanceof Comparable&&o2_value instanceof Comparable){
                        Comparable v1= (Comparable) o1_value;
                        Comparable v2= (Comparable) o2_value;
                        result = v1.compareTo(v2);
                        if(result==0){//相等的话，比较下一项
                            continue;
                        }
                        if(result>0){
                            return asc?1:-1;
                        }
                        if(result<0){
                            return asc?-1:1;
                        }
                    }
                }
                return result;
            }
        };
        Collections.sort(totalRowRecords, comparator);
    }

    private LinkedList<RowRecord> mergeResultSets(ResultSetMetaData metaData, List<PreparedStatement> preparedStatementList) throws SQLException {
        LinkedList<RowRecord> results=new LinkedList<RowRecord>();
        for (PreparedStatement preparedStatement : preparedStatementList) {
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()){
                RowRecord rowRecord=new RowRecord();
                for (int i = 0; i <metaData.getColumnCount(); i++) {
                    String columnName=metaData.getColumnName(i);
                    Object columnValue = resultSet.getObject(i+1);
                    rowRecord.putColumnValue(i+1,columnName,columnValue);
                }
                results.add(rowRecord);
            }
        }
        return results;
    }

    private DragonResultSetMetaData makeResultSetMetaData(ResultSetMetaData metaData) throws SQLException {
        int columnCount = metaData.getColumnCount();
        List<ColumnMetaData> columnMetaDataList=new ArrayList<ColumnMetaData>(columnCount);
        DragonResultSetMetaData result=new DragonResultSetMetaData(columnMetaDataList);
        //columnIndex的索引从1开始

        for (int i = 1; i <= columnCount; i++) {
            ColumnMetaData columnMetaData=new ColumnMetaData();

            columnMetaData.setCatalogName(metaData.getCatalogName(i));//// TODO: 2017/3/10 改为逻辑库，逻辑表
            columnMetaData.setTableName(metaData.getTableName(i));//// TODO: 2017/3/10
            columnMetaData.setSchemaName(metaData.getSchemaName(i));// TODO: 2017/3/10

            String columnLabel = metaData.getColumnLabel(i);
            columnMetaData.setColumnLabel(columnLabel);
            columnMetaData.setColumnName(metaData.getColumnName(i));
            columnMetaData.setColumnClassName(metaData.getColumnClassName(i));
            columnMetaData.setColumnType(metaData.getColumnType(i));
            columnMetaData.setColumnTypeName(metaData.getColumnTypeName(i));

            result.putColumnLabelIndexMaping(columnLabel,i);

            columnMetaData.setColumnDisplaySize(metaData.getColumnDisplaySize(i));
            columnMetaData.setCurrency(metaData.isCurrency(i));
            columnMetaData.setNullable(metaData.isNullable(i));
            columnMetaData.setAutoIncrement(metaData.isAutoIncrement(i));
            columnMetaData.setCaseSensitive(metaData.isCaseSensitive(i));
            columnMetaData.setWritable(metaData.isWritable(i));
            columnMetaData.setSearchable(metaData.isSearchable(i));
            columnMetaData.setScale(metaData.getScale(i));

            columnMetaData.setDefinitelyWritable(metaData.isDefinitelyWritable(i));
            columnMetaData.setReadOnly(metaData.isReadOnly(i));
            columnMetaData.setSigned(metaData.isSigned(i));
            columnMetaData.setPrecision(metaData.getPrecision(i));

            columnMetaDataList.add(columnMetaData);
        }
        return result;
    }

    private List<PreparedStatement> getRealPreparedStatementList(Map<String, Map<String, SqlRouteInfo>> sqlRewriteResult) {
        Iterator<Map.Entry<String, Map<String, SqlRouteInfo>>> dbIterator = sqlRewriteResult.entrySet().iterator();
        List<PreparedStatement> preparedStatementList = new ArrayList<PreparedStatement>();
        while (dbIterator.hasNext()) {
            Map.Entry<String, Map<String, SqlRouteInfo>> entry = dbIterator.next();
            String dbIndex = entry.getKey();
            Map<String, SqlRouteInfo> tbSqlMap = entry.getValue();
            Iterator<Map.Entry<String, SqlRouteInfo>> tbIterator = tbSqlMap.entrySet().iterator();
            while (tbIterator.hasNext()) {
                Map.Entry<String, SqlRouteInfo> tableResult = tbIterator.next();
                PreparedStatement targetStatement = tableResult.getValue().getTargetStatement();
                preparedStatementList.add(targetStatement);
            }
        }
        return preparedStatementList;
    }
}
