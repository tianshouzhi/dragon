package com.tianshouzhi.dragon.physical;

/**
 * Created by tianshouzhi on 2018/1/23.
 */
public class StringUtil {

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isBlank(String str) {
        if (str == null || str.trim().length() == 0) {
            return true;
        }
        return false;
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
