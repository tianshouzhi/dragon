package com.tianshouzhi.dragon.ha.jdbc.datasource.dbselector;

import com.tianshouzhi.dragon.common.exception.ExceptionSorter;
import com.tianshouzhi.dragon.common.exception.MySqlExceptionSorter;
import org.apache.commons.lang3.StringUtils;

import javax.sql.CommonDataSource;
import javax.sql.DataSource;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class DatasourceWrapper {
    private int readWeight=10;
    private int writeWeight=10;
    private DataSource physicalDataSource;
    private boolean isReadOnly;
    private ExceptionSorter exceptionSorter=new MySqlExceptionSorter();
    public DatasourceWrapper(String dataSourceIndex, DataSource physicalDataSource) {
        this(dataSourceIndex,10,10, physicalDataSource);
    }
    public DatasourceWrapper(String dataSourceIndex, int readWeight, int writeWeight, DataSource physicalDataSource) {
        check(dataSourceIndex,readWeight,writeWeight, physicalDataSource);
        this.readWeight = readWeight;
        this.writeWeight = writeWeight;
        this.physicalDataSource = physicalDataSource;
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
            throw new IllegalArgumentException("parameter 'physicalDataSource' can't be null");
        }
    }

    public int getReadWeight() {
        return readWeight;
    }

    public int getWriteWeight() {
        return writeWeight;
    }

    public DataSource getPhysicalDataSource() {
        return physicalDataSource;
    }

    public boolean isReadOnly(){
        return isReadOnly;
    }
    @Override
    public String toString() {
        return "DatasourceWrapper{" +
                ", readWeight=" + readWeight +
                ", writeWeight=" + writeWeight +
                ", physicalDataSource=" + physicalDataSource.getClass().getName() +
                '}';
    }

    public ExceptionSorter getExceptionSorter() {
        return exceptionSorter;
    }

}
