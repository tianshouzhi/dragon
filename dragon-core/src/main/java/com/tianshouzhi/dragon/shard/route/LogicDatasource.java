package com.tianshouzhi.dragon.shard.route;

import com.tianshouzhi.dragon.common.util.MapUtils;
import com.tianshouzhi.dragon.common.util.StringUtils;
import com.tianshouzhi.dragon.shard.exception.DragonShardException;

import javax.sql.DataSource;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by TIANSHOUZHI336 on 2017/2/19.
 */
public class LogicDatasource extends LogicConfig {
	private String defaultDSName; // 主要用于处理只分表，不分库的情况，可能只有部分表的数据比较多，需要进行分表

	/**
	 * 数据库名称与对应的数据源的映射关系,TreeMap会根据key排序
	 */
	private Map<String, DataSource> dsNameDatasourceMap = new TreeMap<String, DataSource>();

	public LogicDatasource(String dbNameFormat, Map<String, DataSource> dsNameDatasourceMap, String defaultDSName)
	      throws DragonShardException {
		super(dbNameFormat);
		if (MapUtils.isEmpty(dsNameDatasourceMap)) {
			throw new IllegalArgumentException("dsNameDatasourceMap can't be null or empty!!!");
		}
		this.dsNameDatasourceMap.putAll(dsNameDatasourceMap);
		// 如果defaultDSName！=null，但是dsNameDatasourceMap不包含defaultDSName，说明配置错误
		if (StringUtils.isNotBlank(defaultDSName)) {
			if (!dsNameDatasourceMap.containsKey(defaultDSName)) {
				throw new IllegalArgumentException("dsNameDatasourceMap doesn't contains defaultDSName:" + defaultDSName);
			}
			this.defaultDSName = defaultDSName;
		} else {// 如果没有提供defaultDSName，并且dsNameDatasourceMap只有一个元素，说明只分表，不分库，则将defaultDSName设置为这个库名
			if (dsNameDatasourceMap.size() == 1) {
				this.defaultDSName = dsNameDatasourceMap.keySet().iterator().next();
			}
		}
		// todo 检查dbIndex(即map的key)和namePattern是否匹配
	}

	public DataSource getDatasource(String dbIndex) {
		return dsNameDatasourceMap.get(dbIndex);
	}

	public Map<String, DataSource> getRealDbIndexDatasourceMap() {
		return dsNameDatasourceMap;
	}

	public String getDefaultDSName() {
		return defaultDSName;
	}
}
