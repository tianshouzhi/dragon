package com.tianshouzhi.dragon.common.cache.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheStats;
import com.google.common.collect.ImmutableMap;
import com.tianshouzhi.dragon.common.cache.DragonCache;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

/**
 * 完全委派给com.google.common.cache.Cache或者其子类来处理
 */
public class GoogleDragonCache<K, V> implements DragonCache<K, V> {

	private Cache<K, V> cache;

	public GoogleDragonCache(Cache<K, V> cache) {
		if (cache == null) {
			throw new NullPointerException();
		}
		this.cache = cache;
	}

	public void putAll(Map<? extends K, ? extends V> map) {
		cache.putAll(map);
	}

	public ConcurrentMap<K, V> asMap() {
		return cache.asMap();
	}

	public ImmutableMap<K, V> getAllPresent(Iterable<?> iterable) {
		return cache.getAllPresent(iterable);
	}

	public void cleanUp() {
		cache.cleanUp();
	}

	public V get(K k, Callable<? extends V> callable) throws ExecutionException {
		return cache.get(k, callable);
	}

	public void invalidate(K k) {
		cache.invalidate(k);
	}

	public CacheStats stats() {
		return cache.stats();
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

	@Override
	public void put(K k, V v) {
		cache.put(k, v);
	}

	@Override
	public V get(K k) {
		return cache.getIfPresent(k);
	}
}
