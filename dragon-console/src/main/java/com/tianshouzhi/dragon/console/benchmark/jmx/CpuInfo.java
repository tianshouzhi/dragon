package com.tianshouzhi.dragon.console.benchmark.jmx;

/**
 * Created by tianshouzhi on 2017/6/15.
 */
public class CpuInfo {
    private static long staticsTimeMillis;
    private static long cpuTimeNs;

    public static long getStaticsTimeMillis() {
        return staticsTimeMillis;
    }

    public static void setStaticsTimeMillis(long staticsTimeMillis) {
        CpuInfo.staticsTimeMillis = staticsTimeMillis;
    }

    public static long getCpuTimeNs() {
        return cpuTimeNs;
    }

    public static void setCpuTimeNs(long cpuTimeNs) {
        CpuInfo.cpuTimeNs = cpuTimeNs;
    }
}
