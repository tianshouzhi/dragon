package com.tianshouzhi.dragon.common.jdbc.sqltype;

import com.tianshouzhi.dragon.common.cache.DragonCache;
import com.tianshouzhi.dragon.common.cache.DragonCacheBuilder;
import com.tianshouzhi.dragon.common.log.Log;
import com.tianshouzhi.dragon.common.log.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class SqlTypeUtil {
	private static final Log LOGGER = LoggerFactory.getLogger(SqlTypeUtil.class);

	private static DragonCache<String, Boolean> sqlTypeCache = DragonCacheBuilder.build(100, 2000, 50, 10, TimeUnit.MINUTES);;

	public static boolean isQuery(String sql, boolean useCache) throws SQLException {
		Boolean isQuery = null;
		if (useCache) {
			isQuery = sqlTypeCache.get(sql);
		}
		if (isQuery != null) {
			return isQuery;
		}

		SqlType sqlType = parseSqlType(sql);
		// if can't decide sql type, default go write
		isQuery = (sqlType == null) ? false : sqlType.isQuery();

		if (useCache) {
			sqlTypeCache.put(sql, isQuery);
		}
		return isQuery;
	}

	public static SqlType parseSqlType(String sql) throws SQLException {
		SqlType[] values = SqlType.values();
		for (SqlType current : values) {
			Pattern pattern = current.getPattern();
			Matcher matcher = pattern.matcher(sql);
			if (matcher.matches()) {
				return current;
			}
		}

		return null;
	}
}
