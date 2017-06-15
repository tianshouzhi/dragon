package com.tianshouzhi.dragon.console;

import com.tianshouzhi.dragon.console.benchmark.BenchmarkOptions;
import com.tianshouzhi.dragon.console.benchmark.BenchmarkRule;
import com.tianshouzhi.dragon.console.benchmark.BenchmarkRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.Random;

/**
 * Created by tianshouzhi on 2017/6/14.
 */
public class BenchmarkTest {
    @Rule
    public TestRule testRule=new BenchmarkRule();

    @Test
    @BenchmarkOptions
    public void test1() throws InterruptedException {
        Thread.sleep(10);
    }
    @Test
    @BenchmarkOptions
    public void test2() throws InterruptedException {
        Thread.sleep(20);
        int i = new Random().nextInt(10);
        if(i<0){
            throw new RuntimeException("mock exception");
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.run(BenchmarkTest.class);
    }
}
