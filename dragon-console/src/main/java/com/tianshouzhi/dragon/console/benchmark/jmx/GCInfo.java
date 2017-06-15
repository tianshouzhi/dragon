package com.tianshouzhi.dragon.console.benchmark.jmx;

import java.util.Arrays;

/**
 * Created by tianshouzhi on 2017/6/14.
 */
public class GCInfo {
    private String gcName;
    private long gcCount;
    private long gcTime;
    private String[] memoryPoolNames;

    public GCInfo(String gcName, long gcCount, long gcTime, String[] memoryPoolNames) {
        this.gcName = gcName;
        this.gcCount = gcCount;
        this.gcTime = gcTime;
        this.memoryPoolNames = memoryPoolNames;
    }

    public String getGcName() {
        return gcName;
    }

    public void setGcName(String gcName) {
        this.gcName = gcName;
    }

    public long getGcCount() {
        return gcCount;
    }

    public void setGcCount(long gcCount) {
        this.gcCount = gcCount;
    }

    public long getGcTime() {
        return gcTime;
    }

    public void setGcTime(long gcTime) {
        this.gcTime = gcTime;
    }

    public String[] getMemoryPoolNames() {
        return memoryPoolNames;
    }

    public void setMemoryPoolNames(String[] memoryPoolNames) {
        this.memoryPoolNames = memoryPoolNames;
    }

    @Override
    public String toString() {
        return "GCInfo{" +
                "gcName='" + gcName + '\'' +
                ", gcCount=" + gcCount +
                ", gcTime=" + gcTime +
                ", memoryPoolNames=" + Arrays.toString(memoryPoolNames) +
                '}';
    }
}
