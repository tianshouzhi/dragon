package com.tianshouzhi.dragon.common.log;

/**
 * Created by tianshouzhi on 2017/8/12.
 */
public interface Log {
    boolean isDebugEnabled();

    boolean isTraceEnabled();

    void error(String s, Throwable e);

    void error(String s);

    void debug(String s);

    void trace(String s);

    void warn(String s);

    void info(String s);
}
