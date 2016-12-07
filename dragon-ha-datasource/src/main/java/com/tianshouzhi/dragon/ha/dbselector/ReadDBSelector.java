package com.tianshouzhi.dragon.ha.dbselector;

import java.util.List;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class ReadDBSelector extends AbstractDBSelector{
    public ReadDBSelector(List<DatasourceWrapper> datasourceWrapperList) {
        super(datasourceWrapperList);
    }

    @Override
    protected boolean isCadidate(DatasourceWrapper datasourceWrapper) {
        return datasourceWrapper.getReadWeight() > 0;
    }

    @Override
    protected int getWeight(DatasourceWrapper datasourceWrapper) {
        return datasourceWrapper.getReadWeight();
    }

}
