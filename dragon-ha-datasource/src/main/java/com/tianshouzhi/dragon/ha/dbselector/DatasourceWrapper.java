package com.tianshouzhi.dragon.ha.dbselector;

import com.tianshouzhi.dragon.common.exception.ExceptionSorter;
import com.tianshouzhi.dragon.common.exception.MySqlExceptionSorter;
import com.tianshouzhi.dragon.common.jdbc.datasource.DataSourceIndex;
import org.apache.commons.lang3.StringUtils;

import javax.sql.CommonDataSource;
import javax.sql.DataSource;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class DatasourceWrapper {
    private DataSourceIndex dataSourceIndex;
    private int readWeight=10;
    private int writeWeight=10;
    private DataSource realDataSource;
    private boolean isReadOnly;
    private ExceptionSorter exceptionSorter=new MySqlExceptionSorter();
    public DatasourceWrapper(String dataSourceIndex, DataSource realDataSource) {
        this(dataSourceIndex,10,10,realDataSource);
    }
    public DatasourceWrapper(String dataSourceIndex, int readWeight, int writeWeight, DataSource realDataSource) {
        check(dataSourceIndex,readWeight,writeWeight,realDataSource);
        this.dataSourceIndex = new DataSourceIndex(dataSourceIndex);
        this.readWeight = readWeight;
        this.writeWeight = writeWeight;
        this.realDataSource = realDataSource;
        this.isReadOnly=readWeight>0&&writeWeight==0;
    }

    private void check(String index, int readWeight, int writeWeight, CommonDataSource realDataSource) {
        if(StringUtils.isBlank(index)){
            throw new IllegalArgumentException("parameter 'dataSourceIndex' can't be empty or blank");
        }
        if(readWeight<0||writeWeight<0||(readWeight==writeWeight&&readWeight==0)){
            throw new IllegalArgumentException("either 'readWeight' or 'writeWeight' can't less than zero,and can't be zero at the same time,current readWeight:"+readWeight+",current writeWeight:"+writeWeight);
        }
        if(realDataSource==null){
            throw new IllegalArgumentException("parameter 'realDataSource' can't be null");
        }
    }

    public int getReadWeight() {
        return readWeight;
    }

    public int getWriteWeight() {
        return writeWeight;
    }

    public DataSource getRealDataSource() {
        return realDataSource;
    }
    public DataSourceIndex getDataSourceIndex() {
        return dataSourceIndex;
    }

    public boolean isReadOnly(){
        return isReadOnly;
    }
    @Override
    public String toString() {
        return "DatasourceWrapper{" +
                "dataSourceIndex=" + dataSourceIndex +
                ", readWeight=" + readWeight +
                ", writeWeight=" + writeWeight +
                ", realDataSource=" + realDataSource.getClass().getName() +
                '}';
    }

    public ExceptionSorter getExceptionSorter() {
        return exceptionSorter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DatasourceWrapper that = (DatasourceWrapper) o;

        return dataSourceIndex != null ? dataSourceIndex.equals(that.dataSourceIndex) : that.dataSourceIndex == null;

    }

    @Override
    public int hashCode() {
        return dataSourceIndex != null ? dataSourceIndex.hashCode() : 0;
    }
}
