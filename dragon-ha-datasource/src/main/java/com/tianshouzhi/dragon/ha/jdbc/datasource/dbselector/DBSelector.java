package com.tianshouzhi.dragon.ha.jdbc.datasource.dbselector;

import com.tianshouzhi.dragon.common.jdbc.datasource.DataSourceIndex;

import java.util.Set;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public interface DBSelector {
    public DataSourceIndex select();
    public Set<DataSourceIndex> getManagedDBIndexes();

    static class WeightRange{
        int start;
        int end;

        public WeightRange(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return "WeightRange{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }
    }

}
