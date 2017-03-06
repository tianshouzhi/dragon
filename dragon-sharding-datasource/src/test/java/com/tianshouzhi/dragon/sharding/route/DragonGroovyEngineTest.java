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
      /*  params.put("id","11101");
        Object eval = DragonGroovyEngine.eval("${id}.toLong().intdiv(100)%100",params);
        System.out.println(eval);*/
        Object _100 = DragonGroovyEngine.eval("1056+542", null);
        System.out.println("_100:"+_100);
        Object _101 = DragonGroovyEngine.eval("1020+971+914+768+148+112+73+64+4124+3887+1067+47+77+781+730+520+423+381+2765713+432817+1+1020+971+914+767+148+112+73+64+4125+3887+1067+47+77+781+731+520+423+381+2765714+432857+1+465+1120+9", null);
        System.out.println("_101:"+_101);
        Object _102 = DragonGroovyEngine.eval("32+34511+491+109243+1313841+77815+233868+57699+14+219+72863+784+273531+3581039+232567+234309+19607+73+34+5+108+1+23", null);
        System.out.println("_102:"+_102);
        Object _105 = DragonGroovyEngine.eval("305+305+2740+28+271+718+159+18+105+1", null);
        System.out.println("_105:"+_105);
        Object _106 = DragonGroovyEngine.eval("161+1298+67+34+47+85+1035+2527+2188+4622+28+120+1616+95+351+3", null);
        System.out.println("_106:"+_106);
        Object _109 = DragonGroovyEngine.eval("33", null);
        System.out.println("_109:"+_109);
        Object _2 = DragonGroovyEngine.eval("3263486+705372+708461+677016+636739+674090+330419+317547+400277+373370+357686+352899+360202+327033+295132+327123+298039+283482+5407648+66038+644+205799+3523061+225935+490+337089+832+1401+1286587+272779+269011+289688+232502+161264+64715+64252+101090+118980+127651+44877+44849+151043+183640+135254+112981+99333+409026+27738+365+41903+1258439+71407+63+92035+64+2600+44+6802+57411+259+20+18906+211+5+1+79", null);
        System.out.println("_2:"+_2);
    }

}