package com.tianshouzhi.dragon.console.benchmark.jmx;

import com.sun.management.*;

import javax.management.ObjectName;
import java.lang.management.*;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Map<String, GCInfo> getGcInfo(){
        List<GarbageCollectorMXBean> gcMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        Map<String,GCInfo> result=new HashMap<String,GCInfo>();
        for (GarbageCollectorMXBean gcMXBean : gcMXBeans) {
            String name = gcMXBean.getName();
            long collectionTime = gcMXBean.getCollectionTime();
            long collectionCount = gcMXBean.getCollectionCount();
            String[] memoryPoolNames = gcMXBean.getMemoryPoolNames();
            result.put(name,new GCInfo(name,collectionTime,collectionCount,memoryPoolNames));
        }
        return result;
    }

    public static RuntimeInfo getRuntimeInfo(){
        OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
        String osname = osMXBean.getName();
        int availableProcessors = osMXBean.getAvailableProcessors();
        String osarch = osMXBean.getArch();
        String osversion = osMXBean.getVersion();
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        String vmName = runtimeMXBean.getVmName();
        String vmVersion = runtimeMXBean.getSpecVersion();
        return new RuntimeInfo(osname,osarch,osversion,availableProcessors,vmName,vmVersion);
    }

    public static MemeryInfo getMemeryInfo(){
        return null;
    }

    public static long getCpuTimeNs(){
        OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
        return ((com.sun.management.OperatingSystemMXBean) osMXBean).getProcessCpuTime();
    }
}
