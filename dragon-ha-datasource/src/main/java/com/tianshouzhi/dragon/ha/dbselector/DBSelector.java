package com.tianshouzhi.dragon.ha.dbselector;

import java.util.Set;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public interface DBSelector {
    public DBIndex select();
    public Set<DBIndex> getManagedDBIndexes();

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
