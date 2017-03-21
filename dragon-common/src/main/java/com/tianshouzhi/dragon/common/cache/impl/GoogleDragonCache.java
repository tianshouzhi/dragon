package com.tianshouzhi.dragon.common.cache.impl;

import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.sun.istack.internal.Nullable;
import com.tianshouzhi.dragon.common.cache.DragonCache;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

/**
 * 完全委派给google LoadingCache来处理
 */
public class GoogleDragonCache<K,V> implements DragonCache<K,V> {

    private LoadingCache<K,V> cache;

    public void putAll(Map<? extends K, ? extends V> map) {
        cache.putAll(map);
    }

    public V getUnchecked(K k) {
        return cache.getUnchecked(k);
    }

    public ImmutableMap<K, V> getAllPresent(Iterable<?> iterable) {
        return cache.getAllPresent(iterable);
    }

    public ImmutableMap<K, V> getAll(Iterable<? extends K> iterable) throws ExecutionException {
        return cache.getAll(iterable);
    }

    public V get(K k, Callable<? extends V> callable) throws ExecutionException {
        return cache.get(k, callable);
    }

    @Nullable
    public V getIfPresent(Object o) {
        return cache.getIfPresent(o);
    }

    public void invalidate(Object o) {
        cache.invalidate(o);
    }

    public CacheStats stats() {
        return cache.stats();
    }

    public ConcurrentMap<K, V> asMap() {
        return cache.asMap();
    }

    public void cleanUp() {
        cache.cleanUp();
    }

    public void refresh(K k) {
        cache.refresh(k);
    }

    public void invalidateAll(Iterable<?> iterable) {
        cache.invalidateAll(iterable);
    }

    public long size() {
        return cache.size();
    }

    public void invalidateAll() {
        cache.invalidateAll();
    }

    public GoogleDragonCache(LoadingCache<K, V> cache) {
        if(cache==null){
            throw new NullPointerException();
        }
        this.cache = cache;

    }


    @Override
    public void put(K k, V v) {

    }

    @Override
    public V get(K k) {
        return null;
    }
}
