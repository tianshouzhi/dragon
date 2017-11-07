package com.tianshouzhi.dragon.common.jdbc.sqltype;

import java.util.regex.Pattern;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public enum SqlType {
	// 只列出读的sql，其他sql一律走主库
	SELECT, SHOW, DEBUG, EXPLAIN;

	// DUMP, INSERT, UPDATE, DELETE, REPLACE, TRUNCATE, CREATE, DROP, LOAD, MERGE, ALTER, RENAME, CALL;

	private Pattern pattern;

	SqlType() {
		String regex = "(\\s*/\\*.+\\*/)?" + "\\s*" + this.name() + ".+";
		int flags = Pattern.CASE_INSENSITIVE | Pattern.DOTALL;
		pattern = Pattern.compile(regex, flags);
	}

	public Pattern getPattern() {
		return pattern;
	}
}
