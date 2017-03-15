package com.tianshouzhi.dragon.sharding.jdbc.resultset;

import com.tianshouzhi.dragon.common.jdbc.WrapperAdapter;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by TIANSHOUZHI336 on 2017/3/10.
 */
public class DragonResultSetMetaData extends WrapperAdapter implements ResultSetMetaData{
    //column 从1开始计算
    private Map<Integer,ColumnMetaData> columnMetaDataMap =null;
    private Map<String,Integer> columnLabelIndexMap=new HashMap<String, Integer>();
    public DragonResultSetMetaData(Map<Integer,ColumnMetaData>  columnMetaDataMap) {
        if(columnMetaDataMap ==null){
            throw new NullPointerException();
        }
        this.columnMetaDataMap = columnMetaDataMap;
    }

    @Override
    public int getColumnCount() throws SQLException {
        return columnMetaDataMap.size();
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return columnMetaDataMap.get(column).isAutoIncrement();
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        return columnMetaDataMap.get(column).isCaseSensitive();
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        return columnMetaDataMap.get(column).isSearchable();
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        return columnMetaDataMap.get(column).isCurrency();
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return columnMetaDataMap.get(column).isNullable();
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        return columnMetaDataMap.get(column).isSigned();
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        return columnMetaDataMap.get(column).getColumnDisplaySize();
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return columnMetaDataMap.get(column).getColumnLabel();
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return columnMetaDataMap.get(column).getColumnName();
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        return columnMetaDataMap.get(column).getSchemaName();
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        return columnMetaDataMap.get(column).getPrecision();
    }

    @Override
    public int getScale(int column) throws SQLException {
        return columnMetaDataMap.get(column).getScale();
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return columnMetaDataMap.get(column).getTableName();
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        return columnMetaDataMap.get(column).getCatalogName();
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        return columnMetaDataMap.get(column).getColumnType();
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        return columnMetaDataMap.get(column).getColumnTypeName();
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return columnMetaDataMap.get(column).isReadOnly();
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        return columnMetaDataMap.get(column).isWritable();
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return columnMetaDataMap.get(column).isDefinitelyWritable();
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        return columnMetaDataMap.get(column).getColumnClassName();
    }

    public void putColumnLabelIndexMaping(String columnLabel, int i) {
        columnLabelIndexMap.put(columnLabel,i);
    }

    public int getColumnIndex(String columnLabel) {
        if(columnLabelIndexMap.containsKey(columnLabel)){
            return columnLabelIndexMap.get(columnLabel);
        }
        return 0;
    }
}
