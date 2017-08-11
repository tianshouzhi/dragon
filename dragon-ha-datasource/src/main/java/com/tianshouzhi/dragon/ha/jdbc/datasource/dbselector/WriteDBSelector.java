package com.tianshouzhi.dragon.ha.jdbc.datasource.dbselector;

import com.tianshouzhi.dragon.ha.jdbc.datasource.RealDatasourceWrapper;

import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class WriteDBSelector extends AbstractDBSelector {
	public WriteDBSelector(Map<String, RealDatasourceWrapper> indexDsMap) {
		super(indexDsMap);
	}

	@Override
	protected boolean isCadidate(RealDatasourceWrapper realDatasourceWrapper) {
		return realDatasourceWrapper.getWriteWeight() > 0;
	}

	@Override
	protected int getWeight(RealDatasourceWrapper realDatasourceWrapper) {
		return realDatasourceWrapper.getWriteWeight();
	}
}
