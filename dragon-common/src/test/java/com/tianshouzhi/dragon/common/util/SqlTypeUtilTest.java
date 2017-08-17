package com.tianshouzhi.dragon.common.util;

import com.tianshouzhi.dragon.common.jdbc.sqltype.SqlType;
import com.tianshouzhi.dragon.common.jdbc.sqltype.SqlTypeUtil;
import org.junit.Test;

/**
 * Created by tianshouzhi on 2017/6/25.
 */
public class SqlTypeUtilTest {
	@Test
	public void parseSqlType() throws Exception {
		String sql = "/*master*/ INSERT INTO article(title,abstracts,content,visible,qr_code_url,create_time," +
				"last_update_time)\n"
		      + "        VALUES (?,?,?,?,?,?,?);";
		SqlType sqlType = SqlTypeUtil.parseSqlType(sql);
		assert sqlType == SqlType.INSERT;
	}

}