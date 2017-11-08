package com.tianshouzhi.dragon.ha.router.impl;

import com.tianshouzhi.dragon.ha.exception.DataSourceMonitor;
import com.tianshouzhi.dragon.ha.router.Router;

import java.util.Set;

/**
 * Created by tianshouzhi on 2017/11/5.
 */
public abstract class BaseRouter implements Router {
    protected String haDSName;

    public BaseRouter(String haDSName) {
        this.haDSName = haDSName;
    }

    @Override
    public String route() {
        Set<String> excludes=DataSourceMonitor.getInvalidRealDs(haDSName);
        return doRoute(excludes);
    }

    protected abstract String doRoute(Set<String> excludes);
}
