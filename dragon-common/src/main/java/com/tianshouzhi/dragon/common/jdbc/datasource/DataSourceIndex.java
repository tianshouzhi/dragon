package com.tianshouzhi.dragon.common.jdbc.datasource;

/**
 * 此类表示数据源的编号，本人的开发习惯是，见名知意，如果直接用String的话，不知道含义是什么
 */
public class DataSourceIndex {
    private String indexStr;

    public DataSourceIndex(String indexStr) {
        this.indexStr = indexStr;
    }

    public String getIndexStr() {
        return indexStr;
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
