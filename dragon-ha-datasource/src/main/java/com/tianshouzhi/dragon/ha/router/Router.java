package com.tianshouzhi.dragon.ha.router;

import java.util.Set;

/**
 * Created by tianshouzhi on 2017/8/16.
 */
public interface Router {
    String route(Set<String> exculdes);
}
