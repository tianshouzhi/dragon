package com.tianshouzhi.site.domain.dto;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * Created by tianshouzhi on 2017/6/17.
 */
@Intercepts(@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
        RowBounds.class, ResultHandler.class }))
public class PageRequest {
    private Long offset;
    private Long rows;
    private List<Sort> sortList;
    private List<Condition> conditionList;
    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getRows() {
        return rows;
    }

    public void setRows(Long rows) {
        this.rows = rows;
    }

    public List<Sort> getSortList() {
        return sortList;
    }

    public void setSortList(List<Sort> sortList) {
        this.sortList = sortList;
    }

    public List<Condition> getConditionList() {
        return conditionList;
    }

    public void setConditionList(List<Condition> conditionList) {
        this.conditionList = conditionList;
    }

    public static class Sort{
        private String columnName;
        private Boolean isAsc;

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public Boolean getAsc() {
            return isAsc;
        }

        public void setAsc(Boolean asc) {
            isAsc = asc;
        }
    }

    public static class Condition{
        private String columnName;
        private String columnValue;
        private ConditionType conditionType=ConditionType.EQUALS;

        public Condition(String columnName, String columnValue) {
            this.columnName = columnName;
            this.columnValue = columnValue;
        }

        public Condition(String columnName, String columnValue, ConditionType conditionType) {
            this.columnName = columnName;
            this.columnValue = columnValue;
            this.conditionType = conditionType;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnValue() {
            return columnValue;
        }

        public void setColumnValue(String columnValue) {
            this.columnValue = columnValue;
        }

        public ConditionType getConditionType() {
            return conditionType;
        }

        public void setConditionType(ConditionType conditionType) {
            this.conditionType = conditionType;
        }
    }

    public static enum ConditionType{
        EQUALS,GT,LT,GE,LE,LIKE
    }
}
