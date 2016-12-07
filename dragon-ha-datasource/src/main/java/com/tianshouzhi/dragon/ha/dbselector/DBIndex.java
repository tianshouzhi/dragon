package com.tianshouzhi.dragon.ha.dbselector;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class DBIndex {
    private String indexStr;

    public DBIndex(String indexStr) {
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
        return "DBIndex{" +
                "indexStr='" + indexStr + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DBIndex that = (DBIndex) o;

        return indexStr != null ? indexStr.equals(that.indexStr) : that.indexStr == null;

    }

    @Override
    public int hashCode() {
        return indexStr != null ? indexStr.hashCode() : 0;
    }
}
