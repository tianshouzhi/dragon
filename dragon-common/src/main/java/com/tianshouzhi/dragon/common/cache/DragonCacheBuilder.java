package com.tianshouzhi.dragon.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.tianshouzhi.dragon.common.cache.impl.GoogleDragonCache;

import java.util.concurrent.TimeUnit;

/**
 * Created by TIANSHOUZHI336 on 2017/3/20.
 */
public abstract class DragonCacheBuilder {
    public static  <K,V> DragonCache<K,V> build(int initialCapacity, int maximumSize, int concurrencyLevel, int expireAfterAccess, TimeUnit timeUnit){
        Cache<K, V> cache = CacheBuilder.newBuilder()
                .initialCapacity(initialCapacity)//初始容量，最多可以自动扩容到maximumSize
                .maximumSize(maximumSize)//最大容量
                .concurrencyLevel(concurrencyLevel)//并发级别，最多允许多少个线程并发访问
                .expireAfterAccess(expireAfterAccess, timeUnit)//缓存项在给定时间内没有被读/写访问，则回收。
                .recordStats()//设置要统计缓存的命中率
                .build();
        return new GoogleDragonCache(cache);
    }
}
