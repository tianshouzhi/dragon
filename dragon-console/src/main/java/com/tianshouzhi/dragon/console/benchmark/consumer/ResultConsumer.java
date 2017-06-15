package com.tianshouzhi.dragon.console.benchmark.consumer;

import com.tianshouzhi.dragon.console.benchmark.result.BenchmarkResult;

/**
 * Created by tianshouzhi on 2017/6/15.
 */
public interface ResultConsumer {
    public void consume(BenchmarkResult benchmarkResult) throws Exception;
}
