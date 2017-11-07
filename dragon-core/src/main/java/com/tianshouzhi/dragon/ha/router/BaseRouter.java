package com.tianshouzhi.dragon.ha.router;

import com.tianshouzhi.dragon.ha.exception.DataSourceMonitor;

import java.util.Set;

/**
 * Created by tianshouzhi on 2017/11/5.
 */
public abstract class BaseRouter implements Router{
    protected String haDataSourceName;

    public BaseRouter(String haDSName) {
        this.haDataSourceName = haDSName;
    }

    @Override
    public String route() {
        Set<String> excludes=DataSourceMonitor.getInvalidRealDs(haDataSourceName);
        return doRoute(excludes);
    }

    protected abstract String doRoute(Set<String> excludes);
}
