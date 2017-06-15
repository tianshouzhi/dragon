package com.tianshouzhi.dragon.console.benchmark;

import com.tianshouzhi.dragon.console.benchmark.consumer.DefaultConsumer;
import com.tianshouzhi.dragon.console.benchmark.consumer.ResultConsumer;
import com.tianshouzhi.dragon.console.benchmark.jmx.JMXUtils;
import com.tianshouzhi.dragon.console.benchmark.result.BenchmarkConfig;
import com.tianshouzhi.dragon.console.benchmark.result.BenchmarkResult;
import com.tianshouzhi.dragon.console.benchmark.result.SingleBenchmarkResult;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by tianshouzhi on 2017/6/14.
 */
public class BenchmarkStatement extends Statement{
    private Statement base;
    private Description description;
    private ResultConsumer[] consumers;
    private int warmupRounds;
    private int benchmarkRounds;
    private int concurrency;
    private CompletionService<SingleBenchmarkResult> completionService;
    private BenchmarkResult result =null;
    private List<SingleBenchmarkResult> results=new ArrayList<SingleBenchmarkResult>();

    public BenchmarkStatement(Statement base, Description description,ResultConsumer... consumers) {
        this.base = base;
        this.description = description;
        if(consumers==null||consumers.length==0){
            this.consumers=new ResultConsumer[]{new DefaultConsumer()};
        }else{
            this.consumers = consumers;
        }
        this.result =new BenchmarkResult();
        resolveOptions(description);
    }

    private void resolveOptions(Description description) {
        BenchmarkOptions annotation = description.getAnnotation(BenchmarkOptions.class);
        if(annotation==null){
            return;
        }

        warmupRounds = annotation.warmupRounds();
        benchmarkRounds = annotation.benchmarkRounds();
        concurrency = annotation.concurrency();

        result =new BenchmarkResult();
        result.setClassName(description.getClassName());
        result.setMethodName(description.getMethodName());
        result.setBenchmarkConfig(new BenchmarkConfig(warmupRounds,benchmarkRounds,concurrency));
        result.setRuntimeInfo(JMXUtils.getRuntimeInfo());
    }

    @Override
    public void evaluate() throws Throwable {
        warmup();

        result.setStartGcInfoMap(JMXUtils.getGcInfo());
        result.setStartTime(System.currentTimeMillis());
        result.setStartCpuTime(JMXUtils.getCpuTimeNs());

        runBenchmark();
        waitForResult();

        result.setEndTime(System.currentTimeMillis());
        result.setEndGCInfoMap(JMXUtils.getGcInfo());
        result.setEndCpuTime(JMXUtils.getCpuTimeNs());

        report();
    }

    private void report() {
        for (ResultConsumer consumer : consumers) {
            try {
                consumer.consume(result);
            } catch (Exception e) {
                throw new RuntimeException("consume result error {"+result+"}",e);
            }
        }
    }

    private void waitForResult() throws InterruptedException, ExecutionException {
        for (int i = 0; i < benchmarkRounds; i++) {
            results.add(completionService.take().get());
        }
    }

    private void runBenchmark() {
        ExecutorService executor = new BenchmarkExecutor(concurrency, benchmarkRounds);
        completionService = new ExecutorCompletionService<SingleBenchmarkResult>(executor);
        for (int i = 0; i < benchmarkRounds; i++) {
            completionService.submit(new BenchmarkCallable(){
                @Override
                protected void doCall() throws Throwable {
                    base.evaluate();
                }
            });
        }
    }

    private void warmup() throws Throwable {
        for (int i = 0; i < warmupRounds; i++) {
            base.evaluate();
        }
    }

}
