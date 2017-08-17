package com.tianshouzhi.dragon.ha.hint;

import java.util.regex.Pattern;

/**
 * Created by TIANSHOUZHI336 on 2016/12/4.
 */
public class DragonHAHintUtil {
	private static Pattern SQL_HINT_PATTERN = Pattern.compile("/\\*\\s*master\\s*\\*/.+", Pattern.CASE_INSENSITIVE);

	private static final ThreadLocal<Boolean> hint = new ThreadLocal<Boolean>() {
		@Override
		protected Boolean initialValue() {
			return false;
		}
	};

	public static void setHintMaster(boolean user_master) {
		hint.set(user_master);
	}

	public static boolean isHintMaster() {
		return hint.get();
	}

	public static void clearHintMaster() {
		hint.set(false);
	}

	public static boolean isHintMaster(String sql) {
		return SQL_HINT_PATTERN.matcher(sql).matches();
	}

	public static void main(String[] args) {
		assert isHintMaster("/*master*/SELECT * FROM user") == true;
		assert isHintMaster("/* master */ SELECT * FROM user") == true;
		assert isHintMaster("/* MASTER */ SELECT * FROM user") == true;
		assert isHintMaster("/* master */ \nSELECT *\n FROM user") == true;
		assert isHintMaster("/* master * / SELECT * FROM user") == false;
		assert isHintMaster("/* master1 */ SELECT * FROM user") == false;

		assert isHintMaster("SELECT * FROM user") == false;
		assert isHintMaster("insert into user(name) values('tianshouzhi')") == false;
	}

}