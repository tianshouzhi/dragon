package com.tianshouzhi.dragon.console.benchmark;

/**
 * Created by tianshouzhi on 2017/6/14.
 */
public @interface BenchmarkOptions {
    int warmupRounds()  default 5;
    int benchmarkRounds() default 10;
    int concurrency() default 1;
}
