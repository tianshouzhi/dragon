package com.tianshouzhi.dragon.sharding.pipeline.handler.resultmerge.mysql;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.tianshouzhi.dragon.sharding.jdbc.resultset.ColumnMetaData;
import com.tianshouzhi.dragon.sharding.jdbc.resultset.DragonResultSetMetaData;
import com.tianshouzhi.dragon.sharding.jdbc.resultset.DragonShardingResultSet;
import com.tianshouzhi.dragon.sharding.jdbc.resultset.RowRecord;
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingStatement;
import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import com.tianshouzhi.dragon.sharding.pipeline.handler.resultmerge.ResultMerger;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * 查询结果合并
 */
public class MysqlSelectResultMerger implements ResultMerger {
    @Override
    public void merge(HandlerContext context) throws SQLException {
        DragonShardingStatement dragonShardingStatement = context.getDragonShardingStatement();
        List<Statement> realStatementList = context.getRealStatementList();
       //获得各个分库的结果集
        List<ResultSet> realResultSetList=new ArrayList<ResultSet>(realStatementList.size());
        for (Statement statement : realStatementList) {
            realResultSetList.add(statement.getResultSet());
        }
        //  构造ResultSetMetaData 不同库返回的MetaData信息基本类似，只要取第一个就行了
        DragonResultSetMetaData metaData = makeResultSetMetaData(realResultSetList.get(0).getMetaData());
        //合并查询结果集
        List<RowRecord> totalRowRecords = mergeResultSets(metaData, realResultSetList);

        DragonShardingResultSet shardingResultSet = new DragonShardingResultSet(dragonShardingStatement,metaData,realResultSetList);

        //处理order by和limit 以及max、min、groupBy等函数
        if (!CollectionUtils.isEmpty(totalRowRecords)//没有查询到结果，不需要处理
                &&realResultSetList.size() > 1) {//只有一个statement，数据库已经排序好，不需要再次排序和解析limit
            SQLSelectStatement parsedSqlStatement = (SQLSelectStatement) context.getParsedSqlStatement();
            MySqlSelectQueryBlock selectQuery = (MySqlSelectQueryBlock) parsedSqlStatement.getSelect().getQuery();

            //处理order by
            tackleOrderBy(selectQuery.getOrderBy(), totalRowRecords);

            //处理聚合函数与group by
            tackleAggrAndGroupBy(metaData, totalRowRecords, selectQuery);

            //处理limit
            totalRowRecords = limit(selectQuery.getLimit(), context.getOffset(), context.getRowCount(), totalRowRecords);
        }

        shardingResultSet.setRowRecords(totalRowRecords);
        context.setMergedResultSet(shardingResultSet);
    }

    private LinkedList<RowRecord> mergeResultSets(ResultSetMetaData metaData, List<ResultSet> realResultSetList) throws SQLException {
        LinkedList<RowRecord> results = new LinkedList<RowRecord>();
        for (ResultSet resultSet : realResultSetList) {
            while (resultSet.next()) {
                RowRecord rowRecord = new RowRecord();
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = resultSet.getObject(i + 1);
                    rowRecord.putColumnValue(i + 1, columnName, columnValue);
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

    /**
     * 支持的聚合函数 SUM、MAX、MIN、AVG、COUNT
     * 其中 SUM、MAX、MIN、COUNT四个可以组合使用
     * @param metaData
     * @param totalRowRecords
     * @param selectQuery
     * @throws SQLException
     */
    private void tackleAggrAndGroupBy(DragonResultSetMetaData metaData, List<RowRecord> totalRowRecords, MySqlSelectQueryBlock selectQuery) throws SQLException {

        //聚合函数位置与聚合函数的映射
        Map<Integer,SQLAggregateExpr> clomunIndexAggrMap=null;
        for (int i = 0; i < selectQuery.getSelectList().size(); i++) {
            SQLExpr expr = selectQuery.getSelectList().get(i).getExpr();
            if(expr instanceof SQLAggregateExpr){//聚合函数
                if(clomunIndexAggrMap==null){
                    clomunIndexAggrMap=new HashMap<Integer, SQLAggregateExpr>();
                }
                clomunIndexAggrMap.put(i+1,(SQLAggregateExpr) expr);
            }
        }
        if(!MapUtils.isEmpty(clomunIndexAggrMap)){
            //对分库的查询结果进行分组
            SQLSelectGroupByClause groupBy = selectQuery.getGroupBy();
            Map<Object/**group Key*/,List<RowRecord>> groupByMap=new HashMap<Object, List<RowRecord>>();
            if(groupBy==null){//如果没有group by，则所有都是同一组，key为任意值都可以，不一定要是*
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
            //针对各个分组应用聚合函数
            List<RowRecord> returnRecordList = new ArrayList<RowRecord>();
            for (List<RowRecord> groupByList : groupByMap.values()) {
                RowRecord returnRecord = groupByList.get(0);//以每个分组的第一条作为返回结果
                for (Map.Entry<Integer, SQLAggregateExpr> aggregateExprEntry : clomunIndexAggrMap.entrySet()) {
                    Integer aggregateColumnIndex = aggregateExprEntry.getKey();
                    String columnLabel = metaData.getColumnLabel(aggregateColumnIndex-1);//fixme bug，不应该减-1
                    SQLAggregateExpr sqlAggregateExpr = aggregateExprEntry.getValue();
                    final String methodName = sqlAggregateExpr.getMethodName();
                    for (int i = 1; i < groupByList.size(); i++) {
                        RowRecord rowRecord=groupByList.get(i);
                        if ("COUNT".equals(methodName)){
                            Long value = (Long) rowRecord.getValue(aggregateColumnIndex);
                            Long value1 = (Long) returnRecord.getValue(aggregateColumnIndex);
                            returnRecord.putColumnValue(aggregateColumnIndex,columnLabel,value1+value);
                        }
                        if ("SUM".equals(methodName)){
                            BigDecimal value = (BigDecimal) rowRecord.getValue(aggregateColumnIndex);
                            BigDecimal value1 = (BigDecimal) returnRecord.getValue(aggregateColumnIndex);
                            returnRecord.putColumnValue(aggregateColumnIndex,columnLabel,value1.add(value));
                        }
                        if ("MAX".equals(methodName)){
                            Comparable value = (Comparable) rowRecord.getValue(aggregateColumnIndex);
                            Comparable value1 = (Comparable) returnRecord.getValue(aggregateColumnIndex);
                            if(value.compareTo(value1)>0){
                                returnRecord.putColumnValue(aggregateColumnIndex,columnLabel,value);
                            }
                        }
                        if ("MIN".equals(methodName)){
                            Comparable value = (Comparable) rowRecord.getValue(aggregateColumnIndex);
                            Comparable value1 = (Comparable) returnRecord.getValue(aggregateColumnIndex);
                            if(value.compareTo(value1)<0){
                                returnRecord.putColumnValue(aggregateColumnIndex,columnLabel,value);
                            }
                        }
                        /*if ("AVG".equals(methodName)){
                            Comparable value = (Comparable) rowRecord.getValue(aggregateColumnIndex);
                            Comparable value1 = (Comparable) returnRecord.getValue(aggregateColumnIndex);
                            if(value.compareTo(value1)<0){
                                returnRecord.putColumnValue(aggregateColumnIndex,columnLabel,value);
                            }
                        }*/
                    }

                }
                returnRecordList.add(returnRecord);
                totalRowRecords.clear();
                totalRowRecords.addAll(returnRecordList);
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
}
