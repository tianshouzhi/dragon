package com.tianshouzhi.dragon.console;

import com.tianshouzhi.dragon.console.benchmark.BenchmarkRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * Created by tianshouzhi on 2017/6/14.
 */
public class BenchmarkTest {
    @Rule
    public TestRule testRule=new BenchmarkRule();

    @Test
    public void test1(){
        System.out.println(System.currentTimeMillis());
    }
    @Test
    public void test2(){
        System.out.println(System.currentTimeMillis());
    }
}
