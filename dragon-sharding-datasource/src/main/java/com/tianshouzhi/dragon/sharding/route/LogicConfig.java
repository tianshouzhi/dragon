package com.tianshouzhi.dragon.sharding.route;

import com.tianshouzhi.dragon.common.util.StringUtils;
import com.tianshouzhi.dragon.sharding.exception.DragonShardException;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.regex.Pattern;

/**
 * Created by TIANSHOUZHI336 on 2017/2/23.
 */
public abstract class LogicConfig {
	// route rule中变量的命名规则
	public static final Pattern routeRuleVariablePattern = Pattern.compile("(\\$\\{.+?\\})", Pattern.CASE_INSENSITIVE);

	private String namePattern;

	protected MessageFormat messageFormat;// eg table_{00}

	public LogicConfig(String namePattern) throws DragonShardException {
		if (StringUtils.isBlank(namePattern)) {
			throw new DragonShardException("namePattern can't be blank!!!");
		}
		this.namePattern = namePattern;
		this.messageFormat = new MessageFormat(namePattern);
	}

	public String getNamePattern() {
		return namePattern;
	}

	public String format(Long caculatedIndex) {
		return messageFormat.format(new Object[] { caculatedIndex });
	}

	public Long parseIndex(String realName){
		try {

			Object o = messageFormat.parse(realName)[0];
			if (o instanceof Long) {
				return (Long) o;
			} else {
				return Long.parseLong((String) o);
			}

		} catch (ParseException e) {
			throw new DragonShardException(realName, e);
		}
	}
}
