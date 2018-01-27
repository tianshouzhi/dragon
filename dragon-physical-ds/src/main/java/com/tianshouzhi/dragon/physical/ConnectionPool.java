package com.tianshouzhi.dragon.physical;

import java.sql.Connection;
import java.util.List;

/**
 * Created by tianshouzhi on 2018/1/26.
 */
public interface ConnectionPool {

	void init();

	// 基本连接池参数
	int getMaxPoolSize();

	int getMinPoolSize();

	int getInitPoolSize();

	int getCheckoutTimeout();

	// 保持连接存活参数
	int getValidationInterval();// 连接有效性检测周期，单位ms

	int getValidationTimeout(); // 连接有效性检 超时时间

	int getMinEvictableIdleTimeMillis(); // 连接最小存活时间,在这个时间，不允许evict

	boolean isTestWhenIdle();

	boolean isTestOnBorrow();

	// 统计参数
	int getNumActive(); // 获得已经被借出去的数量

	int getNumIdle(); // 连接池中当前链接数量


    //
	List<String> getConnectionInitSql();

	Connection borrowConnection();

	Connection returnConnection();

}
