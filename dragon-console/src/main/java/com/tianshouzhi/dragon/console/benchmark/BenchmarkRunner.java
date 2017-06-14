package com.tianshouzhi.dragon.console.benchmark;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * Created by tianshouzhi on 2017/6/14.
 */
public class BenchmarkRunner {
    public static void run(Class<?>...classes){
        JUnitCore.runClasses(classes);
    }
}
