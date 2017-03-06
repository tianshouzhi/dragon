package com.tianshouzhi.dragon.sharding.pipeline.handler;

import org.junit.Test;

import java.util.concurrent.*;

/**
 * Created by TIANSHOUZHI336 on 2017/2/28.
 */
public class ExecutorCompletionServiceTest {
    @Test
    public void test() throws InterruptedException, ExecutionException {
        ExecutorService executorService=new ThreadPoolExecutor(100,100,10, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>());
        ExecutorCompletionService<Integer> ecs=new ExecutorCompletionService<Integer>(executorService);
        for (int i = 1; i <= 10; i++) {
            final int finalI = i;
            ecs.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return finalI;
                }
            });
        }
        int sum=0;
        for (int i = 0; i < 10; i++) {
            Integer integer = ecs.take().get();
            sum+=integer;
        }
        System.out.println(sum);
    }
}
