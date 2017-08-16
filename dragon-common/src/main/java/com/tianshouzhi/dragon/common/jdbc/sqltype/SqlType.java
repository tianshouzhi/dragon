package com.tianshouzhi.dragon.common.jdbc.sqltype;

import java.util.regex.Pattern;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public enum SqlType {

	SELECT(true), SHOW(true), DEBUG(true), EXPLAIN(true), DUMP,

	INSERT, UPDATE, DELETE, REPLACE, TRUNCATE, CREATE, DROP, LOAD, MERGE, ALTER, RENAME, CALL;

	private Pattern pattern;

	private boolean isQuery;

	SqlType() {
		this(false);
	}

	SqlType(boolean isQuery) {
		String regex = "(\\s*/\\*.+\\*/)?" + "\\s*" + this.name() + ".+";
		int flags = Pattern.CASE_INSENSITIVE | Pattern.DOTALL;
		pattern = Pattern.compile(regex, flags);
		this.isQuery = isQuery;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public boolean isQuery() {
		return isQuery;
	}
}
