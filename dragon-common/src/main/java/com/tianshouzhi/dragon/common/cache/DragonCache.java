package com.tianshouzhi.dragon.common.cache;

/**
 * Dragon自己并没有实现缓存功能，基于已有的第三方缓存实现，如：google guava中的缓存实现。
 * 提供自己的接口主要是为了方便以后，如果需要切换底层的缓存实现时，可以对用户屏蔽
 */
public interface DragonCache<K,V> {
    /**
     * 存入cache
     * @param k
     * @param v
     */
    public void put(K k,V v);

    /**
     * 获取cache
     * @param k
     * @return
     */
    public V get(K k);
}
