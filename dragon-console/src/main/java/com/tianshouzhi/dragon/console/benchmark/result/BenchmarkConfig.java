package com.tianshouzhi.dragon.console.benchmark.result;

/**
 * Created by tianshouzhi on 2017/6/14.
 */
public class BenchmarkConfig {
    private int warmupRounds;
    private int benchmarkRounds;
    private int concurrency;

    public BenchmarkConfig(int warmupRounds, int benchmarkRounds, int concurrency) {
        this.warmupRounds = warmupRounds;
        this.benchmarkRounds = benchmarkRounds;
        this.concurrency = concurrency;
    }

    public int getWarmupRounds() {
        return warmupRounds;
    }

    public void setWarmupRounds(int warmupRounds) {
        this.warmupRounds = warmupRounds;
    }

    public int getBenchmarkRounds() {
        return benchmarkRounds;
    }

    public void setBenchmarkRounds(int benchmarkRounds) {
        this.benchmarkRounds = benchmarkRounds;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }
}
