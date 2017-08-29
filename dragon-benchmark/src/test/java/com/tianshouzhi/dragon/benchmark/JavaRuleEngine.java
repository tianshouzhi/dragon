package com.tianshouzhi.dragon.benchmark;

import java.util.Map;

/**
 * Created by tianshouzhi on 2017/8/29.
 */
public class JavaRuleEngine implements RuleEngine{

    @Override
    public Object eval(Map<String, Object> params) {
        String id =  params.get("id")+"";
        //"id.substring(id.length()-4).toLong().intdiv(100)%100"
        return Long.parseLong(id.substring(id.length()-4))/100%100;
    }
}
