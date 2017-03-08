package com.tianshouzhi.dragon.sharding.route;

import org.junit.Test;

import java.util.HashMap;

/**
 * Created by TIANSHOUZHI336 on 2017/2/23.
 */
public class DragonGroovyEngineTest {
    @Test
    public void eval() throws Exception {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("id","0101");
        Object eval = DragonGroovyEngine.eval("id.substring(id.length()-4).toLong().intdiv(100)%100",params);
        System.out.println(eval);

    }

}