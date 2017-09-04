package com.tianshouzhi.dragon.benchmark;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkMethodChart;
import com.tianshouzhi.dragon.sharding.route.DragonGroovyEngine;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.FileReader;
import java.util.HashMap;

/**
 * Created by tianshouzhi on 2017/8/29.
 */
@BenchmarkMethodChart(filePrefix = "benchmark-lists")
public class RuleEngineBenchmark extends AbstractBenchmark{

    static RuleEngine javaRuleEngine=new JavaRuleEngine();
    static RuleEngine groovyEngine= (RuleEngine) getGroovyEngine("src/test/resources/GroovyRuleEngine.groovy");
    static HashMap<String, Object> params = new HashMap<String, Object>(){{
        put("id", "0101");
    }};

    static {
        System.setProperty("jub.consumers","CONSOLE,H2");
        System.setProperty("jub.db.file",".benchmarks");
    }

    @Test
    @BenchmarkOptions( benchmarkRounds = 1000000, warmupRounds = 1000,concurrency = 100)
    public void testScriptEngine() {
        DragonGroovyEngine.eval("id.substring(id.length()-4).toLong().intdiv(10)%10", params);

    }

    @Test
    @BenchmarkOptions( benchmarkRounds = 1000000, warmupRounds = 1000,concurrency = 100)
    public void testGroovy() {
        groovyEngine.eval(params);
    }


    @Test
    @BenchmarkOptions( benchmarkRounds = 1000000, warmupRounds = 1000,concurrency = 100)
    public void testJava() throws Exception {
        javaRuleEngine.eval(params);
    }

    private static GroovyObject getGroovyEngine(String path) {
        try {
            FileReader fileReader = new FileReader(path);
            String script = IOUtils.toString( fileReader);
            ClassLoader parent = ClassLoader.getSystemClassLoader();
            GroovyClassLoader loader = new GroovyClassLoader(parent );
            Class<?> clazz = loader .parseClass(script);
            loader.close();
            return (GroovyObject) clazz .newInstance();
        } catch (Exception e ) {
            throw new RuntimeException(e);
        }
    }

}
