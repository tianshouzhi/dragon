package com.tianshouzhi.dragon.ha.dbselector;

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

    protected Map<WeightRange, DBIndex> rangeDBIndexMap = new ConcurrentHashMap<WeightRange, DBIndex>();
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

    private String buildLog(Map<WeightRange, DBIndex> rangeDBIndexMap, List<DatasourceWrapper> filterResult) {
        StringBuilder sb=new StringBuilder("\n");
        sb.append("managed datasource num:"+filterResult.size());
        sb.append(",total weight:"+totalWeight);
        sb.append("\n[\n");
        for (DatasourceWrapper datasourceWrapper : filterResult) {
            DBIndex dbIndex = datasourceWrapper.getDbIndex();
            Integer readWeight = datasourceWrapper.getReadWeight();
            Integer writeWeight = datasourceWrapper.getWriteWeight();
            CommonDataSource realDataSource = datasourceWrapper.getRealDataSource();
            WeightRange caculateRange=null;
            for (Map.Entry<WeightRange, DBIndex> weightRangeDBIndexEntry : rangeDBIndexMap.entrySet()) {
                DBIndex value = weightRangeDBIndexEntry.getValue();
                if(value.equals(dbIndex)){
                    caculateRange=weightRangeDBIndexEntry.getKey();
                    break;
                }
            }
            sb.append("{");
            sb.append("dbIndex:"+dbIndex.getIndexStr()
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
            rangeDBIndexMap.put(weightRange, datasourceWrapper.getDbIndex());
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
    public DBIndex select() {
        int random = new Random().nextInt(totalWeight);
        Set<WeightRange> weightRanges = rangeDBIndexMap.keySet();
        DBIndex result = null;
        for (WeightRange weightRange : weightRanges) {
            if (random >= weightRange.start && random <weightRange.end) {
                result = rangeDBIndexMap.get(weightRange);
            }
        }
        LOGGER.debug("select result is {}", result);
        return result;
    }

    public Set<DBIndex> getManagedDBIndexes(){
        return new HashSet<DBIndex>(rangeDBIndexMap.values());
    }
}