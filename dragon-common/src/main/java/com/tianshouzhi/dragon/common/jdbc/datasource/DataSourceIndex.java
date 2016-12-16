package com.tianshouzhi.dragon.common.jdbc.datasource;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class DataSourceIndex {
    private String indexStr;

    public DataSourceIndex(String indexStr) {
        this.indexStr = indexStr;
    }

    public String getIndexStr() {
        return indexStr;
    }

    public void setIndexStr(String indexStr) {
        this.indexStr = indexStr;
    }

    @Override
    public String toString() {
        return indexStr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataSourceIndex that = (DataSourceIndex) o;

        return indexStr != null ? indexStr.equals(that.indexStr) : that.indexStr == null;

    }

    @Override
    public int hashCode() {
        return indexStr != null ? indexStr.hashCode() : 0;
    }
}
