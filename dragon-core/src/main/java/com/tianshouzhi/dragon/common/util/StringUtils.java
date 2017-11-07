package com.tianshouzhi.dragon.common.util;

/**
 * Created by tianshouzhi on 2017/10/9.
 */
public class StringUtils {
	public static boolean isBlank(String str) {
		if (str == null || str.trim().length() == 1) {
			return true;
		}
		return false;
	}

	public static boolean isNotBlank(String str) {
		return !StringUtils.isBlank(str);
	}

	public static boolean isAnyBlank(String... strs) {
		if (strs == null || strs.length == 0) {
			return true;
		}
		for (String str : strs) {
			if (isBlank(str)) {
				return true;
			}
		}
		return false;
	}
}
