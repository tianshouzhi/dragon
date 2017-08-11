package com.tianshouzhi.dragon.console.benchmark.result;

import com.tianshouzhi.dragon.console.benchmark.jmx.GCInfo;
import com.tianshouzhi.dragon.console.benchmark.jmx.RuntimeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tianshouzhi on 2017/6/14.
 */
public class BenchmarkResult {
    private RuntimeInfo runtimeInfo ;
    private Map<String, GCInfo> startGcInfoMap;
    private Map<String, GCInfo> endGCInfoMap;
    private BenchmarkConfig benchmarkConfig;
    private List<SingleBenchmarkResult> results;
    private long startCpuTime;
    private long endCpuTime;
    private long startTime;
    private long endTime;
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

    public double getRoundAvg() {
        return (endTime-startTime)/benchmarkConfig.getBenchmarkRounds();
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

    public void setClassName(String className) {
        this.className = className;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public void setStartGcInfoMap(Map<String, GCInfo> startGcInfoMap) {
        this.startGcInfoMap = startGcInfoMap;
    }
    

    public void setEndGCInfoMap(Map<String, GCInfo> endGCInfoMap) {
        this.endGCInfoMap = endGCInfoMap;
    }
    
    public List<GCInfo> getGCInfoList(){
        List<GCInfo> gcInfoList=new ArrayList<GCInfo>();
        for (String gcName : endGCInfoMap.keySet()) {
            GCInfo startGcInfo = this.startGcInfoMap.get(gcName);
            GCInfo endGcInfo = this.endGCInfoMap.get(gcName);
            gcInfoList.add(new GCInfo(gcName,
                    endGcInfo.getGcCount() - startGcInfo.getGcCount(),
                    endGcInfo.getGcTime()-startGcInfo.getGcTime(),
                    endGcInfo.getMemoryPoolNames()));
        }
        return gcInfoList;
    }

    public long getStartCpuTime() {
        return startCpuTime;
    }

    public void setStartCpuTime(long startCpuTime) {
        this.startCpuTime = startCpuTime;
    }

    public long getEndCpuTime() {
        return endCpuTime;
    }

    public void setEndCpuTime(long endCpuTime) {
        this.endCpuTime = endCpuTime;
    }

    public double getAverageCpuRatio(){
        double ratio = (endCpuTime-startCpuTime)/1000000.0/(endTime-startTime);
        return ratio;
    }

    @Override
    public String toString() {
        return "BenchmarkResult{" +
                "runtimeInfo=" + runtimeInfo +
                ", startGcInfoMap=" + startGcInfoMap +
                ", endGCInfoMap=" + endGCInfoMap +
                ", benchmarkConfig=" + benchmarkConfig +
                ", results=" + results +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", getClassName='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", errorCount=" + errorCount +
                '}';
    }
}
