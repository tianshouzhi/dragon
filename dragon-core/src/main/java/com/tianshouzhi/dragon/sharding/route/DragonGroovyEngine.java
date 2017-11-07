package com.tianshouzhi.dragon.sharding.route;

import com.tianshouzhi.dragon.sharding.exception.DragonShardException;

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

	public static Object eval(String script, Map<String, Object> params) {
		try {

			if (params != null) {
				ScriptContext context = null;
				context = new SimpleScriptContext();
				for (Map.Entry<String, Object> entry : params.entrySet()) {
					context.setAttribute(entry.getKey(), entry.getValue(), ScriptContext.ENGINE_SCOPE);
				}
				return engine.eval(script, context);
			}
			return engine.eval(script);
		} catch (ScriptException e) {
			throw new DragonShardException("script:" + script + " eval error,params" + params.toString(), e);
		}
	}
}
