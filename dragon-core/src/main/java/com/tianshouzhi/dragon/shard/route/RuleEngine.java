package com.tianshouzhi.dragon.shard.route;

import java.util.Map;

/**
 * Created by tianshouzhi on 2018/1/29.
 */
public interface RuleEngine {
    public Object eval(Map<String,Object> params);
}
