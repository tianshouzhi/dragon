package com.tianshouzhi.dragon.common.util;

import java.util.Map;

/**
 * Created by tianshouzhi on 2017/10/9.
 */
public abstract class MapUtils {
	public static boolean isEmpty(Map<?, ?> map) {
		boolean result = false;
		if (map == null || map.size() == 0) {
			result = true;
		}
		return result;
	}
}
