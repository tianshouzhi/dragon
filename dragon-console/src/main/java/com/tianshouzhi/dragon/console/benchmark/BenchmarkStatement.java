package com.tianshouzhi.dragon.console.benchmark;

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
    private int warmupRounds;
    private int benchmarkRounds;
    private int concurrency;
    private CompletionService<SingleBenchmarkResult> completionService;
    private List<SingleBenchmarkResult> results=new ArrayList<SingleBenchmarkResult>();

    public BenchmarkStatement(Statement base, Description description) {
        this.base = base;
        this.description = description;
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
    }

    @Override
    public void evaluate() throws Throwable {
        warmup();
        runBenchmark();
        waitForResult();
        report();
    }

    private void report() {

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
