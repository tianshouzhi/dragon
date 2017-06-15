package com.tianshouzhi.dragon.console.benchmark.consumer;

import com.tianshouzhi.dragon.console.benchmark.jmx.RuntimeInfo;
import com.tianshouzhi.dragon.console.benchmark.result.BenchmarkConfig;
import com.tianshouzhi.dragon.console.benchmark.result.BenchmarkResult;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tianshouzhi on 2017/6/15.
 */
public class DefaultConsumer implements ResultConsumer{
    @Override
    public void consume(BenchmarkResult benchmarkResult) throws Exception {
        System.out.println("============benchmark for "+benchmarkResult.getClassName()+"."+benchmarkResult
                .getMethodName()
                +"============");
        RuntimeInfo runtimeInfo = benchmarkResult.getRuntimeInfo();
        System.out.println("运行环境：\n\t"+runtimeInfo);
        System.out.println("benchmark配置:\n\t"+benchmarkResult.getBenchmarkConfig());
        System.out.println("运行结果:");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        System.out.println("\t开始时间:"+ simpleDateFormat.format(new Date(benchmarkResult.getStartTime()))
                +",结束时间:"+simpleDateFormat.format(new Date(benchmarkResult.getEndTime())));
        NumberFormat nf = NumberFormat.getNumberInstance();
        // 保留两位小数
        nf.setMaximumFractionDigits(2);
        nf.setRoundingMode(RoundingMode.UP);
        System.out.println("\tcpu利用率："+nf.format(benchmarkResult.getAverageCpuRatio()*100)+"%");
        System.out.println("\troundAvg:"+benchmarkResult.getRoundAvg()+" ms");
        System.out.println("\t错误数:"+benchmarkResult.getErrorCount());
        System.out.println("\tgc信息:"+benchmarkResult.getGCInfoList());

    }
}
