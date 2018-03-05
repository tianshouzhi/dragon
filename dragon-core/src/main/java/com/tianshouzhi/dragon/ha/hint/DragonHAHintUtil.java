package com.tianshouzhi.dragon.ha.hint;

/**
 * Created by TIANSHOUZHI336 on 2016/12/4.
 */
public class DragonHAHintUtil {
	//判断sql是否强制走主库，如果使用正则表达式的话，对于长sql，效率低，cpu使用率较高，因此这里直接用string的indexOf方法来判断
	//可能存在误判，例如sql并不需要走主，但是sql内容中包含/*master*/，但是这是极端情况，这样的sql不会太多，不会对主库造成过大压力
	private static String FORCE_MASTER_PREFIX = "/*master*/";

	public static boolean isForceMaster(String sql) {
		return sql.indexOf(FORCE_MASTER_PREFIX) != -1;
	}

	private static final ThreadLocal<Boolean> hint = new ThreadLocal<Boolean>() {
		@Override
		protected Boolean initialValue() {
			return false;
		}
	};

	public static void forceMaster() {
		hint.set(true);
	}

	public static boolean isForceMaster() {
		return hint.get();
	}

	public static void clear() {
		hint.set(false);
	}
}