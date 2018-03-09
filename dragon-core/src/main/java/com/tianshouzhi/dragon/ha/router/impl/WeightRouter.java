package com.tianshouzhi.dragon.ha.router.impl;

import com.tianshouzhi.dragon.common.exception.DragonException;
import com.tianshouzhi.dragon.common.util.CollectionUtils;
import com.tianshouzhi.dragon.common.util.MapUtils;
import com.tianshouzhi.dragon.ha.exception.HAException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tianshouzhi on 2017/8/16.
 */
public class WeightRouter extends BaseRouter {
	protected Map<WeightRange, String> weightIndexMap = new ConcurrentHashMap<WeightRange, String>();

	protected int totalWeight = 0;

	private ThreadLocal<Random> randoms = new ThreadLocal<Random>() {
		@Override
		protected Random initialValue() {
			return new Random();
		}
	};

	public WeightRouter(String haDSName,Map<String, Integer> realDSNameWeightMap) {
		super(haDSName);
		if (MapUtils.isEmpty(realDSNameWeightMap) || realDSNameWeightMap.size() == 1) {
			throw new HAException("realDSNameWeightMap can't be null! and size must > 1");
		}
		this.totalWeight = calculateTotalWeight(realDSNameWeightMap.values());
		this.weightIndexMap = makeWeightRangeIndexMap(realDSNameWeightMap);
	}

	private int calculateTotalWeight(Collection<Integer> weights) {
		int result = 0;
		for (Integer weight : weights) {
			result += weight;
		}
		return result;
	}

	private Map<WeightRange, String> makeWeightRangeIndexMap(Map<String, Integer> indexWeightMap) {
		HashMap<WeightRange, String> resultMap = new HashMap<WeightRange, String>(4);
		int current = 0;
		for (Map.Entry<String, Integer> entry : indexWeightMap.entrySet()) {
			int start = current;
			int end = current + entry.getValue();
			WeightRange weightRange = new WeightRange(start, end);
			resultMap.put(weightRange, entry.getKey());
			current = end;
		}
		return resultMap;
	}

	@Override
	public String doRoute(Set<String> excludes) {
		if (CollectionUtils.isEmpty(excludes)) {
			return selectByRandom(this.weightIndexMap, this.totalWeight);
		}

		if(weightIndexMap.values().equals(excludes)){
			throw new DragonException("all datasource"+excludes+" are not avaliable!!!");
		}

		HashMap<WeightRange, String> afterExcludeMap = new HashMap<WeightRange, String>();
		int totalWeight = 0;
		for (Map.Entry<WeightRange, String> entry : this.weightIndexMap.entrySet()) {
			String datasourceIndex = entry.getValue();
			WeightRange weightRange = entry.getKey();
			if (!excludes.contains(datasourceIndex)) {
				afterExcludeMap.put(weightRange, datasourceIndex);
				totalWeight += (weightRange.end - weightRange.start);
			}
		}

		return selectByRandom(afterExcludeMap, totalWeight);
	}

	private String selectByRandom(Map<WeightRange, String> weightRangeIndexMap, int totalWeight) {
		int random = this.randoms.get().nextInt(totalWeight);
		String result = null;
		for (Map.Entry<WeightRange, String> entry : weightRangeIndexMap.entrySet()) {
			WeightRange weightRange = entry.getKey();
			String datasourceIndex = entry.getValue();
			if (random >= weightRange.start && random < weightRange.end) {
				result = datasourceIndex;
			}
		}
		return result;
	}

	static class WeightRange {
		private int start;

		private int end;

		public WeightRange(int start, int end) {
			this.start = start;
			this.end = end;
		}

		@Override
		public String toString() {
			return "WeightRange{" + "start=" + start + ", end=" + end + '}';
		}
	}
}
