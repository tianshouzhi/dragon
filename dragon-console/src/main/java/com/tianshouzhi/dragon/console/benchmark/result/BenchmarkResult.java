package com.tianshouzhi.dragon.console.benchmark.result;

import com.tianshouzhi.dragon.console.benchmark.result.SingleBenchmarkResult;

import java.util.List;

/**
 * Created by tianshouzhi on 2017/6/14.
 */
public class BenchmarkResult {
    private SystemConfig systemConfig;

    private List<SingleBenchmarkResult> results;
    private long startTime;
    private long endTime;
    private int warmupRounds;
    private int benchmarkRounds;
    private int concurrency;
    private String className;
    private String methodName;
    private int ygcCalls;//ygc次数
    private int fgcCalls;//fgc次数

    public BenchmarkResult(String className,String methodName,
                           int warmupRounds, int benchmarkRounds,
                           int concurrency,
                           long startTime,
                           long endTime,
                           List<SingleBenchmarkResult> results) {
        this.className = className;
        this.methodName = methodName;
        this.warmupRounds = warmupRounds;
        this.benchmarkRounds = benchmarkRounds;
        this.concurrency = concurrency;
        this.startTime = startTime;
        this.endTime = endTime;
        this.results = results;
    }

    public int getWarmupRounds() {
        return warmupRounds;
    }

    public int getBenchmarkRounds() {
        return benchmarkRounds;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public String getClassName() {
        return className;
    }
    public String getMethodName() {
        return methodName;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
}
