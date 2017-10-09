package com.tianshouzhi.dragon.common.util;

/**
 * Created by tianshouzhi on 2017/10/9.
 */
public abstract class ArrayUtils {

	public static boolean isEmpty(Object[] arr) {
		boolean result = false;
		if (arr == null || arr.length == 0) {
			result = true;
		}
		return result;
	}
}
