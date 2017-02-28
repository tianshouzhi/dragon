package com.tianshouzhi.dragon.sharding.pipeline.handler.execution;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by TIANSHOUZHI336 on 2017/2/28.
 */
public class DragonExecutorFactory {

    private static Map<String,ExecutorService> map=new HashMap<String, ExecutorService>();

    public static ExecutorService getInstance (String logicDatabaseName){
        if(map.get(logicDatabaseName)!=null){
            return map.get(logicDatabaseName);
        }
        synchronized (DragonExecutorFactory.class){
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(100, 100, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(), new DragonThreadFactory(logicDatabaseName));
            map.put(logicDatabaseName,threadPoolExecutor);
            return threadPoolExecutor;
        }
    }

    private static class DragonThreadFactory implements ThreadFactory{
        private String poolNamePrefix="DRAGON-SHARDING-EXECUTION-POOL-";
        private  AtomicInteger threadIndex =new AtomicInteger();
        private String logicDatabaseName;

        public DragonThreadFactory(String logicDatabaseName) {
            if(StringUtils.isBlank(logicDatabaseName)){
                throw new IllegalArgumentException("logicDatabaseName can't be blank!!!");
            }
            this.logicDatabaseName = logicDatabaseName;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread=new Thread(r);
            thread.setName(poolNamePrefix+logicDatabaseName+"-THREAD-"+ threadIndex.getAndIncrement());
            return thread;
        }
    }
}
