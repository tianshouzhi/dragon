package com.tianshouzhi.dragon.common.cahce;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Created by TIANSHOUZHI336 on 2016/12/3.
 */
public class LRUCache<K,V> {
    private static final Logger LOGGER= LoggerFactory.getLogger(LRUCache.class);
    private Cache<K,V> lruCache;
    private Callable<V> callable;
    public LRUCache(int initialCapacity, int maxCacheSize, int concurrencyLevel, Callable<V> callable) {
        lruCache= CacheBuilder.newBuilder()
                    .initialCapacity(initialCapacity)
                    .maximumSize(maxCacheSize)
                    .concurrencyLevel(concurrencyLevel)
//                    .expireAfterWrite(expireSeconds,TimeUnit.SECONDS)//给定时间内没有写访问，则回收
//                    .expireAfterAccess(3, TimeUnit.SECONDS)// 给定时间内没有读访问,缓存过期时间为3秒
                    .build();//当本地缓存命没有中时，调用load方法获取结果并将结果缓存
        this.callable = callable;
    }

    public V get(K key){
        try {
            return lruCache.get(key,callable);
        } catch (ExecutionException e) {
            LOGGER.error("get cache error,key:{}",key,e);
        }
        return null;
    }

    public void put(K k,V v){
        lruCache.put(k,v);
    }

     class LRUCallAble<V> implements Callable<V>{
        K k;
        @Override
        public V call() throws Exception {
//            return doCall(k);
            return null;
        }
    }
}
