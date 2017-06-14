package com.tianshouzhi.dragon.console.benchmark;

import com.tianshouzhi.dragon.console.benchmark.result.SingleBenchmarkResult;

import java.util.concurrent.Callable;

/**
 * Created by tianshouzhi on 2017/6/14.
 */
public abstract class BenchmarkCallable implements Callable<SingleBenchmarkResult>{

    @Override
    public SingleBenchmarkResult call() throws Exception {
        try {
            doCall();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    protected abstract void doCall() throws Throwable;
}
