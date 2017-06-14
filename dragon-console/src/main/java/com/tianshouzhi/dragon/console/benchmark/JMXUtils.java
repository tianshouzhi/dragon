package com.tianshouzhi.dragon.console.benchmark;

import com.tianshouzhi.dragon.console.benchmark.result.SystemConfig;

import javax.management.ObjectName;
import java.lang.management.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tianshouzhi on 2017/6/14.
 */
public class JMXUtils {
//    List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
    static {
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        int availableProcessors = operatingSystemMXBean.getAvailableProcessors();
    }

    public static void main(String[] args) {
        List<GarbageCollectorMXBean> gcMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gcMXBean : gcMXBeans) {
            String name = gcMXBean.getName();
            long collectionTime = gcMXBean.getCollectionTime();
            long collectionCount = gcMXBean.getCollectionCount();
            String[] memoryPoolNames = gcMXBean.getMemoryPoolNames();
            ObjectName objectName = gcMXBean.getObjectName();
            System.out.println("name:"+name);
            System.out.println("collectionTime:"+collectionTime);
            System.out.println("collectionCount:"+collectionCount);
            System.out.println("memoryPoolNames:"+ Arrays.toString(memoryPoolNames));
            System.out.println("objectName:"+objectName);
            System.out.println();
        }
    }
    public SystemConfig getRuntimeInfo(){
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        String osname = operatingSystemMXBean.getName();
        int availableProcessors = operatingSystemMXBean.getAvailableProcessors();
        String osarch = operatingSystemMXBean.getArch();
        String osversion = operatingSystemMXBean.getVersion();
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        String vmName = runtimeMXBean.getVmName();
        String vmVersion = runtimeMXBean.getSpecVersion();
        return new SystemConfig(osname,osarch,osversion,availableProcessors,vmName,vmVersion);
    }
}
