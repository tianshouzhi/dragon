package com.tianshouzhi.dragon.common.thread;

import com.tianshouzhi.dragon.common.util.StringUtils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by TIANSHOUZHI336 on 2017/3/25.
 */
public class DragonThreadFactory implements ThreadFactory {
	private String threadNamePrefix;

	private AtomicInteger threadIndex = new AtomicInteger();

	public DragonThreadFactory(String threadNamePrefix) {
		if (StringUtils.isBlank(threadNamePrefix)) {
			throw new NullPointerException();
		}
		this.threadNamePrefix = threadNamePrefix;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);
		thread.setName(threadNamePrefix + "-" + threadIndex.incrementAndGet());
		return thread;
	}
}
