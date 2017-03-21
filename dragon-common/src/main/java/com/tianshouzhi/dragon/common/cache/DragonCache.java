package com.tianshouzhi.dragon.common.cache;

/**
 * Created by TIANSHOUZHI336 on 2017/3/20.
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
