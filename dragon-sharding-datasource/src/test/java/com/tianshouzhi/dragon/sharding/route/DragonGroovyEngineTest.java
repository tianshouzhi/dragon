package com.tianshouzhi.dragon.sharding.route;

import org.junit.Test;

import java.util.HashMap;

/**
 * Created by TIANSHOUZHI336 on 2017/2/23.
 */
public class DragonGroovyEngineTest {
    @Test
    public void eval() throws Exception {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id","11101");
        Object eval = DragonGroovyEngine.eval("id.toLong().intdiv(100)%100",params);
        System.out.println(eval);
    }

}