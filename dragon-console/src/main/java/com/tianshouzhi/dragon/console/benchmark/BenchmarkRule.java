package com.tianshouzhi.dragon.console.benchmark;

import com.tianshouzhi.dragon.console.benchmark.consumer.ResultConsumer;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Created by tianshouzhi on 2017/6/14.
 */
public class BenchmarkRule implements TestRule{
    private ResultConsumer[] resultConsumers;

    public BenchmarkRule(ResultConsumer... resultConsumers) {
        this.resultConsumers = resultConsumers;
    }

    /**
     * 每个加了@Test注解的方法执行前，都会调用apply
     * @param base
     * @param description
     * @return
     */
    @Override
    public Statement apply(Statement base, Description description) {
        return new BenchmarkStatement(base,description, resultConsumers);
    }
}
