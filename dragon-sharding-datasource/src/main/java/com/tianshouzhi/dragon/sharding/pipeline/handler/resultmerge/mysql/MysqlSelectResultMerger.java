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
import com.tianshouzhi.dragon.sharding.jdbc.statement.DragonShardingStatement;
import com.tianshouzhi.dragon.sharding.pipeline.HandlerContext;
import com.tianshouzhi.dragon.sharding.pipeline.handler.resultmerge.ResultMerger;
import com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite.mysql.DragonDruidASTUtil;
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
        DragonShardingStatement dragonShardingStatement = context.getShardingStatement();
        List<Statement> realStatementList = context.getRealStatementList();
       //获得各个分库的结果集
        List<ResultSet> realResultSetList=new ArrayList<ResultSet>(realStatementList.size());
        for (Statement statement : realStatementList) {
            realResultSetList.add(statement.getResultSet());
        }
        //  构造ResultSetMetaData 不同库返回的MetaData信息基本类似，只要取第一个就行了
        DragonResultSetMetaData metaData = makeResultSetMetaData(realResultSetList.get(0).getMetaData());
        //合并查询结果集，将不同的ResultSet实例的结果都合并到totalRowRecords中
        List<DragonShardingResultSet.RowRecord> totalRowRecords=new ArrayList<DragonShardingResultSet.RowRecord>();
        DragonShardingResultSet shardingResultSet = new DragonShardingResultSet(dragonShardingStatement,metaData,realResultSetList,totalRowRecords);
        mergeResultSets(shardingResultSet,totalRowRecords, realResultSetList);

        context.setOriginQueryCount(totalRowRecords.size());
        //处理order by和limit 以及max、min、groupBy等函数
        if (!CollectionUtils.isEmpty(totalRowRecords)//没有查询到结果，不需要处理
                &&realResultSetList.size() > 1) {//只有一个statement，数据库已经排序好，不需要再次排序和解析limit
            SQLSelectStatement parsedSqlStatement = (SQLSelectStatement) context.getParsedSqlStatement();
            MySqlSelectQueryBlock selectQuery = (MySqlSelectQueryBlock) parsedSqlStatement.getSelect().getQuery();

            //处理order by
            tackleOrderBy(selectQuery.getOrderBy(), totalRowRecords,context.getFullColumnNameAliasMap());

            //处理聚合函数与group by
            tackleAggrAndGroupBy(metaData, totalRowRecords, selectQuery,context.getFullColumnNameAliasMap());

            //处理limit
             limit(selectQuery.getLimit(), context.getOffset(), context.getRowCount(), totalRowRecords);
        }
        context.setMergedResultSet(shardingResultSet);
    }

    /**
     * shardingResultSet中已经包含了totalRowRecords的引用，通过修改引用的方法来修改totalRowRecords中的值，而不是直接shardingResultSet直接提供一个set方法来设置totalRowRecords
     * 原因是基于以下考虑：shardingResultSet不提供ResultSet接口定义之外的方法，避免污染接口
     */
    private void mergeResultSets(DragonShardingResultSet shardingResultSet,List<DragonShardingResultSet.RowRecord> totalRowRecords, List<ResultSet> realResultSetList) throws SQLException {
        ResultSetMetaData metaData = shardingResultSet.getMetaData();
        for (ResultSet resultSet : realResultSetList) {
            while (resultSet.next()) {
                DragonShardingResultSet.RowRecord rowRecord = shardingResultSet.new RowRecord();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = resultSet.getObject(i );
                    rowRecord.putColumnValue(i, columnName, columnValue);
                }
                totalRowRecords.add(rowRecord);
            }
        }
    }

    private DragonResultSetMetaData makeResultSetMetaData(ResultSetMetaData metaData) throws SQLException {
        int columnCount = metaData.getColumnCount();
        Map<Integer,ColumnMetaData> columnMetaDataMap=new TreeMap<Integer, ColumnMetaData>();
        DragonResultSetMetaData result=new DragonResultSetMetaData(columnMetaDataMap);
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

            columnMetaDataMap.put(i,columnMetaData);
        }
        return result;
    }

    private void tackleOrderBy(SQLOrderBy orderBy, List<DragonShardingResultSet.RowRecord> totalRowRecords, final Map<String, String> fullColumnNameAliasMap) {
        if(orderBy==null){
            return ;
        }
        final List<SQLSelectOrderByItem> items = orderBy.getItems();

        Comparator<DragonShardingResultSet.RowRecord> comparator = new Comparator<DragonShardingResultSet.RowRecord>() {
            @Override
            public int compare(DragonShardingResultSet.RowRecord o1, DragonShardingResultSet.RowRecord o2) {
                int result=0;
                for (SQLSelectOrderByItem item : items) {
                    boolean asc=true;
                    if(item.getType() != null){
                        asc= SQLOrderingSpecification.ASC==item.getType();
                    }
                    String columnLabel = getColumnLabel(item.getExpr(),fullColumnNameAliasMap);
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
    private void tackleAggrAndGroupBy(DragonResultSetMetaData metaData, List<DragonShardingResultSet.RowRecord> totalRowRecords, MySqlSelectQueryBlock selectQuery,Map<String, String> fullColumnNameAliasMap) throws SQLException {

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
            Map<Object/**group Key*/,List<DragonShardingResultSet.RowRecord>> groupByMap=new HashMap<Object, List<DragonShardingResultSet.RowRecord>>();
            if(groupBy==null){//如果没有group by，则所有都是同一组，key为任意值都可以，不一定要是*
                groupByMap.put("*",totalRowRecords);
            }else{
                SQLExpr groupByColumnExpr = groupBy.getItems().get(0);
                String groupByColumnLabel = getColumnLabel(groupByColumnExpr,fullColumnNameAliasMap);//group by只支持根据一个列，因此只取第一个
                for (DragonShardingResultSet.RowRecord rowRecord : totalRowRecords) {
                    Object groupKey = rowRecord.getValue(groupByColumnLabel);//把这一列当做group Key
                    List<DragonShardingResultSet.RowRecord> rowRecords = groupByMap.get(groupKey);
                    if(rowRecords==null){
                        rowRecords=new ArrayList<DragonShardingResultSet.RowRecord>();
                        groupByMap.put(groupKey,rowRecords);
                    }
                    rowRecords.add(rowRecord);
                }
            }
            //针对各个分组应用聚合函数
            List<DragonShardingResultSet.RowRecord> returnRecordList = new ArrayList<DragonShardingResultSet.RowRecord>();
            for (List<DragonShardingResultSet.RowRecord> groupByList : groupByMap.values()) {
                DragonShardingResultSet.RowRecord returnRecord = groupByList.get(0);//以每个分组的第一条作为返回结果
                for (Map.Entry<Integer, SQLAggregateExpr> aggregateExprEntry : clomunIndexAggrMap.entrySet()) {
                    Integer aggregateColumnIndex = aggregateExprEntry.getKey();
                    String columnLabel = metaData.getColumnLabel(aggregateColumnIndex);
                    SQLAggregateExpr sqlAggregateExpr = aggregateExprEntry.getValue();
                    final String methodName = sqlAggregateExpr.getMethodName();
                    for (int i = 1; i < groupByList.size(); i++) {
                        DragonShardingResultSet.RowRecord rowRecord=groupByList.get(i);
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

    private void limit(MySqlSelectQueryBlock.Limit limit, long offset, long rowcount, List<DragonShardingResultSet.RowRecord> totalRowRecords) {
        if(limit==null){
            return ;
        }
        final long start=offset;
        final long end=Math.min(offset+rowcount,totalRowRecords.size());
        //// TODO: 2017/3/14 有什么更高效的方法，不需要这样频繁的拷贝 ，特别是在记录非常多的情况下
        List<DragonShardingResultSet.RowRecord> resultList=new ArrayList<DragonShardingResultSet.RowRecord>();
        for (int i = 0; i < totalRowRecords.size(); i++) {
            if(i>=start&&i<end){
                resultList.add(totalRowRecords.get(i));
            }
        }
        totalRowRecords.clear();
        totalRowRecords.addAll(resultList);

    }

    private String getColumnLabel(SQLExpr sqlExpr,Map<String, String> fullColumnNameAliasMap){
        String key = sqlExpr.toString();
        if(MapUtils.isNotEmpty(fullColumnNameAliasMap)&&fullColumnNameAliasMap.containsKey(key)){
            return fullColumnNameAliasMap.get(key);
        }
        return DragonDruidASTUtil.getColumnName(sqlExpr);
    }
}
