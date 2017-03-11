package com.tianshouzhi.dragon.sharding.jdbc.resultset;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2017/3/10.
 */
public class RowRecord {
    private Map<Integer,Column> resultsMap=new HashMap<Integer, Column>();
    public void putColumnValue(Integer columnIndex, String columnLabel, Object columnValue) {
        resultsMap.put(columnIndex,new Column(columnLabel,columnValue));
    }
    public Object getValue(Integer columnIndex){
        Column column = resultsMap.get(columnIndex);
        return column.columnValue;
    }

    public Object getValue(String columnLabel){
        Iterator<Column> iterator = resultsMap.values().iterator();
        while (iterator.hasNext()){
            return iterator.next().columnValue;
        }
        return null;
    }

    private static class Column{
        String columnLabel;
        Object columnValue;

        public Column(String columnLabel, Object columnValue) {
            this.columnLabel = columnLabel;
            this.columnValue = columnValue;
        }
    }
}
