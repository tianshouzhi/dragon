package com.tianshouzhi.dragon.ha.jdbc.datasource.dbselector;

import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class ReadDBSelector extends AbstractDBSelector {
	public ReadDBSelector(Map<String, RealDatasourceWrapper> indexDsMap) {
		super(indexDsMap);
	}

	@Override
	protected boolean isCadidate(RealDatasourceWrapper realDatasourceWrapper) {
		return realDatasourceWrapper.getReadWeight() > 0;
	}

	@Override
	protected int getWeight(RealDatasourceWrapper realDatasourceWrapper) {
		return realDatasourceWrapper.getReadWeight();
	}

}
