package com.tianshouzhi.dragon.perf;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkHistoryChart;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkMethodChart;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * Created by tianshouzhi on 2017/6/13.
 */
@BenchmarkMethodChart(filePrefix = "GetConnectionTest.methods")
@BenchmarkHistoryChart(filePrefix = "GetConnectionTest",maxRuns = 20)
public class GetConnectionTest {
    static {
        System.setProperty("jub.consumers", "CONSOLE,MYSQL");
        System.setProperty("jub.mysql.url",
                "jdbc:mysql://localhost:3306/test?user=root&password=shxx12151022&useUnicode" +
                "=true&characterEncoding=utf8");
        System.setProperty("jub.customkey", "version number");
    }

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    @Test
    @BenchmarkOptions(benchmarkRounds = 20, warmupRounds = 3,concurrency = 10)
    public void twentyMillis() throws Exception {
        Thread. sleep(20);
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 20, warmupRounds = 3,concurrency = 10)
    public void thirtyMillis() throws Exception {
        Thread. sleep(30);
    }
}
