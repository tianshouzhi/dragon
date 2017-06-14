package com.tianshouzhi.dragon.console.benchmark;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tianshouzhi on 2017/6/14.
 */
public class BenchmarkExecutor extends ThreadPoolExecutor{
    public BenchmarkExecutor(int concurrency, int benchmarkRounds) {
        super(concurrency, concurrency, 10000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(benchmarkRounds),
                new BenchmarkThreadFactory("BEANCHMARKPOOL"));
    }

    private static class BenchmarkThreadFactory implements ThreadFactory{
        private static AtomicInteger poolId=new AtomicInteger();
        private AtomicInteger threadId=new AtomicInteger();
        private String prefix;

        public BenchmarkThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            String threadName=prefix+"-"+poolId.incrementAndGet()+"-THREAD-"+threadId.incrementAndGet();
            Thread thread = new Thread(r);
            thread.setName(threadName);
            return thread;
        }
    }
}
