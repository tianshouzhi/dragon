package com.tianshouzhi.dragon.ha.dbselector;

import com.tianshouzhi.dragon.common.jdbc.datasource.DataSourceIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.CommonDataSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public abstract class AbstractDBSelector implements DBSelector {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDBSelector.class);

    protected Map<WeightRange, DataSourceIndex> rangeDBIndexMap = new ConcurrentHashMap<WeightRange, DataSourceIndex>();
    protected int totalWeight = 0;

    public AbstractDBSelector(List<DatasourceWrapper> datasourceWrapperList) {
        List<DatasourceWrapper> filterResult = filter(datasourceWrapperList);

        if (filterResult == null || filterResult.size() == 0) {
            LOGGER.warn("no datasource configed for {}",this.getClass().getSimpleName());
        }else{
            totalWeight = caculateTotalWeight(filterResult);
            fillRangeIndexMap(filterResult);
        }
        if(LOGGER.isInfoEnabled()){
            String log=buildLog(rangeDBIndexMap,filterResult);
            LOGGER.info("{} build success:{}",this.getClass().getSimpleName(), log);
        }
    }

    private String buildLog(Map<WeightRange, DataSourceIndex> rangeDBIndexMap, List<DatasourceWrapper> filterResult) {
        StringBuilder sb=new StringBuilder("\n");
        sb.append("managed datasource num:"+filterResult.size());
        sb.append(",total weight:"+totalWeight);
        sb.append("\n[\n");
        for (DatasourceWrapper datasourceWrapper : filterResult) {
            DataSourceIndex dataSourceIndex = datasourceWrapper.getDataSourceIndex();
            Integer readWeight = datasourceWrapper.getReadWeight();
            Integer writeWeight = datasourceWrapper.getWriteWeight();
            CommonDataSource realDataSource = datasourceWrapper.getRealDataSource();
            WeightRange caculateRange=null;
            for (Map.Entry<WeightRange, DataSourceIndex> weightRangeDBIndexEntry : rangeDBIndexMap.entrySet()) {
                DataSourceIndex value = weightRangeDBIndexEntry.getValue();
                if(value.equals(dataSourceIndex)){
                    caculateRange=weightRangeDBIndexEntry.getKey();
                    break;
                }
            }
            sb.append("{");
            sb.append("dataSourceIndex:"+ dataSourceIndex.getIndexStr()
                    +",readWeight:"+readWeight
                    +",writeWeight:"+writeWeight
                    +",type:" +realDataSource.getClass().getSimpleName()
                    +(datasourceWrapper.isReadOnly()?"read":"write")
                    +",select probability:"+(caculateRange.end-caculateRange.start)/((float)totalWeight));
            sb.append("}\n");
        }
        sb.append("]\n");
        return sb.toString();
    }


    private void fillRangeIndexMap(List<DatasourceWrapper> filterResult) {
        int current = 0;
        for (DatasourceWrapper datasourceWrapper : filterResult) {
            int start = current;
            int end = current + getWeight(datasourceWrapper);
            WeightRange weightRange = new WeightRange(start, end);
            rangeDBIndexMap.put(weightRange, datasourceWrapper.getDataSourceIndex());
            current = end;
        }
    }

    protected List<DatasourceWrapper> filter(List<DatasourceWrapper> datasourceWrapperList) {
        if(datasourceWrapperList ==null){
            return null;
        }
        List<DatasourceWrapper> result = new ArrayList<DatasourceWrapper>();
        for (DatasourceWrapper datasourceWrapper : datasourceWrapperList) {
            if (isCadidate(datasourceWrapper)) {
                result.add(datasourceWrapper);
            }
        }
        return result;
    }

    protected abstract boolean isCadidate(DatasourceWrapper datasourceWrapper);

    private int caculateTotalWeight(List<DatasourceWrapper> datasourceWrapperList) {
        int tempTotalWeight = 0;
        for (DatasourceWrapper datasourceWrapper : datasourceWrapperList) {
            tempTotalWeight += getWeight(datasourceWrapper);
        }
        return tempTotalWeight;
    }

    protected abstract int getWeight(DatasourceWrapper datasourceWrapper);

    @Override
    public DataSourceIndex select() {
        int random = new Random().nextInt(totalWeight);
        Set<WeightRange> weightRanges = rangeDBIndexMap.keySet();
        DataSourceIndex result = null;
        for (WeightRange weightRange : weightRanges) {
            if (random >= weightRange.start && random <weightRange.end) {
                result = rangeDBIndexMap.get(weightRange);
            }
        }
        return result;
    }

    public Set<DataSourceIndex> getManagedDBIndexes(){
        return new HashSet<DataSourceIndex>(rangeDBIndexMap.values());
    }
}