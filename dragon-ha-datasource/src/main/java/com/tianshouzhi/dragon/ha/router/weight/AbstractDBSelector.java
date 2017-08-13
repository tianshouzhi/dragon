package com.tianshouzhi.dragon.ha.router.weight;

import com.tianshouzhi.dragon.ha.jdbc.datasource.RealDatasourceWrapper;
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

	protected Map<WeightRange, String> rangeDBIndexMap = new ConcurrentHashMap<WeightRange, String>();

	protected int totalWeight = 0;

	public AbstractDBSelector(Map<String, RealDatasourceWrapper> indexDsMap) {
		Map<String, RealDatasourceWrapper> filterResult = filter(indexDsMap);

		if (filterResult == null || filterResult.size() == 0) {
			LOGGER.warn("no datasource configed for {}", this.getClass().getSimpleName());
		} else {
			totalWeight = caculateTotalWeight(filterResult.values());
			fillRangeIndexMap(filterResult);
		}
		if (LOGGER.isInfoEnabled()) {
			String log = buildLog(rangeDBIndexMap, filterResult);
			LOGGER.info("{} build success:{}", this.getClass().getSimpleName(), log);
		}
	}

	private String buildLog(Map<WeightRange, String> rangeDBIndexMap, Map<String, RealDatasourceWrapper> filterResult) {
		StringBuilder sb = new StringBuilder(200);
		sb.append("\n[\n");
		for (Map.Entry<String, RealDatasourceWrapper> entry : filterResult.entrySet()) {
			RealDatasourceWrapper realDatasourceWrapper = entry.getValue();
			String dataSourceIndex = entry.getKey();
			Integer readWeight = realDatasourceWrapper.getReadWeight();
			Integer writeWeight = realDatasourceWrapper.getWriteWeight();
			CommonDataSource realDataSource = realDatasourceWrapper.getRealDataSource();
			WeightRange caculateRange = null;
			for (Map.Entry<WeightRange, String> weightRangeDBIndexEntry : rangeDBIndexMap.entrySet()) {
				String value = weightRangeDBIndexEntry.getValue();
				if (value.equals(dataSourceIndex)) {
					caculateRange = weightRangeDBIndexEntry.getKey();
					break;
				}
			}
			sb.append("{");
			sb.append("dataSourceIndex:" + dataSourceIndex + ",readWeight:" + readWeight + ",writeWeight:" + writeWeight
			      + ",type:" + realDataSource.getClass().getSimpleName() + ","
			      + (realDatasourceWrapper.isReadOnly() ? "read" : "write") + ",select probability:"
			      + (caculateRange.end - caculateRange.start) / ((float) totalWeight));
			sb.append("}\n");
		}
		sb.append("]\n");
		return sb.toString();
	}

	private void fillRangeIndexMap(Map<String, RealDatasourceWrapper> filterResult) {
		int current = 0;
		for (Map.Entry<String, RealDatasourceWrapper> entry : filterResult.entrySet()) {
			int start = current;
			int end = current + getWeight(entry.getValue());
			WeightRange weightRange = new WeightRange(start, end);
			rangeDBIndexMap.put(weightRange, entry.getKey());
			current = end;
		}
	}

	protected Map<String, RealDatasourceWrapper> filter(Map<String, RealDatasourceWrapper> indexDsMap) {
		if (indexDsMap == null) {
			return null;
		}
		Map<String, RealDatasourceWrapper> result = new HashMap<String, RealDatasourceWrapper>();
		for (Map.Entry<String, RealDatasourceWrapper> entry : indexDsMap.entrySet()) {
			if (isCadidate(entry.getValue())) {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	protected abstract boolean isCadidate(RealDatasourceWrapper realDatasourceWrapper);

	private int caculateTotalWeight(Collection<RealDatasourceWrapper> realDatasourceWrapperList) {
		int tempTotalWeight = 0;
		for (RealDatasourceWrapper realDatasourceWrapper : realDatasourceWrapperList) {
			tempTotalWeight += getWeight(realDatasourceWrapper);
		}
		return tempTotalWeight;
	}

	protected abstract int getWeight(RealDatasourceWrapper realDatasourceWrapper);

	@Override
	public String select() {
		int random = new Random().nextInt(totalWeight);
		Set<WeightRange> weightRanges = rangeDBIndexMap.keySet();
		String result = null;
		for (WeightRange weightRange : weightRanges) {
			if (random >= weightRange.start && random < weightRange.end) {
				result = rangeDBIndexMap.get(weightRange);
			}
		}
		return result;
	}

	public Set<String> getManagedDBIndexes() {
		return new HashSet<String>(rangeDBIndexMap.values());
	}
}