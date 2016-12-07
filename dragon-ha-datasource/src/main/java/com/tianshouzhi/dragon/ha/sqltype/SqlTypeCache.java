package com.tianshouzhi.dragon.ha.sqltype;

import com.tianshouzhi.dragon.common.cahce.LRUCache;

import java.util.concurrent.Callable;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class SqlTypeCache{
    private static int defaultInitialCapacity=100;
    private static int defaultMaxCacheSize=1000;
    private static int defaultConcurrencyLevel=10;
    private static SqlTypeCacheLoader cacheLoader=new SqlTypeCacheLoader();
    private LRUCache<String,Boolean> lruCache;

    public SqlTypeCache() {
        lruCache=new LRUCache(defaultInitialCapacity, defaultMaxCacheSize, defaultConcurrencyLevel, cacheLoader);
    }

    private static class SqlTypeCacheLoader implements Callable<Boolean> {

        @Override
        public Boolean call() throws Exception {
            return null;
        }
    }

    public Boolean get(String sql){
        return lruCache.get(sql);
    }
}
