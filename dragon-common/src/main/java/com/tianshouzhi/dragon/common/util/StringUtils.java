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
}
