package com.tianshouzhi.dragon.perf;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * Created by tianshouzhi on 2017/6/13.
 */
public class JUnitCoreTest {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(GetConnectionTest.class);
        System.out.println(result);
    }
}
