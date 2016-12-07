package com.tianshouzhi.dragon.ha.dbselector;

import com.tianshouzhi.dragon.common.exception.ExceptionSorter;
import com.tianshouzhi.dragon.common.exception.MySqlExceptionSorter;
import org.apache.commons.lang3.StringUtils;

import javax.sql.CommonDataSource;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class DatasourceWrapper {
    private DBIndex dbIndex;
    private Integer readWeight;
    private Integer writeWeight;
    private CommonDataSource realDataSource;
    private boolean isReadOnly;
    private ExceptionSorter exceptionSorter=new MySqlExceptionSorter();

    public DatasourceWrapper(String dbIndex, int readWeight, int writeWeight, CommonDataSource realDataSource) {
        check(dbIndex,readWeight,writeWeight,realDataSource);
        this.dbIndex = new DBIndex(dbIndex);
        this.readWeight = readWeight;
        this.writeWeight = writeWeight;
        this.realDataSource = realDataSource;
        this.isReadOnly=readWeight>0&&writeWeight==0;
    }

    private void check(String index, int readWeight, int writeWeight, CommonDataSource realDataSource) {
        if(StringUtils.isBlank(index)){
            throw new IllegalArgumentException("parameter 'dbIndex' can't be empty or blank");
        }
        if(readWeight<0||writeWeight<0||(readWeight==writeWeight&&readWeight==0)){
            throw new IllegalArgumentException("either 'readWeight' or 'writeWeight' can't less than zero,and can't be zero at the same time,current readWeight:"+readWeight+",current writeWeight:"+writeWeight);
        }
        if(realDataSource==null){
            throw new IllegalArgumentException("parameter 'realDataSource' can't be null");
        }
    }

    public Integer getReadWeight() {
        return readWeight;
    }

    public void setReadWeight(Integer readWeight) {
        this.readWeight = readWeight;
    }

    public Integer getWriteWeight() {
        return writeWeight;
    }

    public void setWriteWeight(Integer writeWeight) {
        this.writeWeight = writeWeight;
    }

    public CommonDataSource getRealDataSource() {
        return realDataSource;
    }

    public void setRealDataSource(CommonDataSource realDataSource) {
        this.realDataSource = realDataSource;
    }

    public DBIndex getDbIndex() {
        return dbIndex;
    }

    public boolean isReadOnly(){
        return isReadOnly;
    }
    @Override
    public String toString() {
        return "DatasourceWrapper{" +
                "dbIndex=" + dbIndex +
                ", readWeight=" + readWeight +
                ", writeWeight=" + writeWeight +
                ", realDataSource=" + realDataSource.getClass().getName() +
                '}';
    }

    public ExceptionSorter getExceptionSorter() {
        return exceptionSorter;
    }

    public void setExceptionSorter(ExceptionSorter exceptionSorter) {
        this.exceptionSorter = exceptionSorter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DatasourceWrapper that = (DatasourceWrapper) o;

        return dbIndex != null ? dbIndex.equals(that.dbIndex) : that.dbIndex == null;

    }

    @Override
    public int hashCode() {
        return dbIndex != null ? dbIndex.hashCode() : 0;
    }
}
