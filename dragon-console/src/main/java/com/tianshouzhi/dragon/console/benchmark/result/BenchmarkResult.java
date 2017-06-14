package com.tianshouzhi.dragon.console.benchmark.result;

import com.tianshouzhi.dragon.console.benchmark.jmx.JMXUtils;
import com.tianshouzhi.dragon.console.benchmark.jmx.RuntimeInfo;

import java.util.List;

/**
 * Created by tianshouzhi on 2017/6/14.
 */
public class BenchmarkResult {
    private RuntimeInfo runtimeInfo = JMXUtils.getRuntimeInfo();
    private BenchmarkConfig benchmarkConfig;
    private List<SingleBenchmarkResult> results;
    private long startTime;
    private long endTime;
    private long roundAvg;
    private String className;
    private String methodName;
    private int errorCount;//错误数


    public RuntimeInfo getRuntimeInfo() {
        return runtimeInfo;
    }

    public BenchmarkConfig getBenchmarkConfig() {
        return benchmarkConfig;
    }

    public void setBenchmarkConfig(BenchmarkConfig benchmarkConfig) {
        this.benchmarkConfig = benchmarkConfig;
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

    public long getRoundAvg() {
        return roundAvg;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setRuntimeInfo(RuntimeInfo runtimeInfo) {
        this.runtimeInfo = runtimeInfo;
    }

    public List<SingleBenchmarkResult> getResults() {
        return results;
    }

    public void setResults(List<SingleBenchmarkResult> results) {
        this.results = results;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setRoundAvg(long roundAvg) {
        this.roundAvg = roundAvg;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }
}
