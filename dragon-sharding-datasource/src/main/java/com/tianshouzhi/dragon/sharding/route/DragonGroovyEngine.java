package com.tianshouzhi.dragon.sharding.route;

import javax.script.*;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2017/2/23.
 */
public abstract class DragonGroovyEngine {
    private static final ScriptEngine engine;
    static {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("Groovy");
    }

    public static Object eval(String script, Map<String,String> params) throws ScriptException {
        ScriptContext context=null;
        if(params!=null){
            context = new SimpleScriptContext();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                context.setAttribute(entry.getKey(),entry.getValue(),ScriptContext.ENGINE_SCOPE);
            }
        }
        return engine.eval(script,context);
    }
}
