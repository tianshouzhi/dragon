package com.tianshouzhi.dragon.console.benchmark;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by tianshouzhi on 2017/6/14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface BenchmarkOptions {
    int warmupRounds()  default 5;
    int benchmarkRounds() default 10;
    int concurrency() default 1;
}
