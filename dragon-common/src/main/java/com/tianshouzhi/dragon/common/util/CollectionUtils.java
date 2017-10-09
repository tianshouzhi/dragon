package com.tianshouzhi.dragon.common.util;

import java.util.Collection;

/**
 * Created by tianshouzhi on 2017/10/9.
 */
public class CollectionUtils {
    public static boolean isEmpty(Collection<?> col) {
        boolean result = false;
        if (col == null || col.size() == 0) {
            result = true;
        }
        return result;
    }
}
