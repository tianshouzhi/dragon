package com.tianshouzhi.dragon.shard.route;

import java.util.Map;

/**
 * Created by tianshouzhi on 2018/1/29.
 */
public class AbstractRuleEngine implements RuleEngine{
    @Override
    public Object eval(Map<String, Object> params) {
        return ((Long)params.get(""))%10;
    }
}
