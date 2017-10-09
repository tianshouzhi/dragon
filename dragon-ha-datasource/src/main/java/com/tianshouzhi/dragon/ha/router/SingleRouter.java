package com.tianshouzhi.dragon.ha.router;

import com.tianshouzhi.dragon.common.util.CollectionUtils;
import com.tianshouzhi.dragon.common.util.StringUtils;
import com.tianshouzhi.dragon.ha.exception.DragonHARuntimeException;

import java.util.Set;

/**
 * Created by tianshouzhi on 2017/8/16.
 */
public class SingleRouter implements Router {
	private String datasourceIndex;

	public SingleRouter(String datasourceIndex) {
		if (StringUtils.isBlank(datasourceIndex)) {
			throw new DragonHARuntimeException("index can't be blank!");
		}
		this.datasourceIndex = datasourceIndex;
	}

	@Override
	public String route(Set<String> exculdes) {
		if (CollectionUtils.isEmpty(exculdes)) {
			return datasourceIndex;
		}

		if (exculdes.size() > 1) {
			throw new DragonHARuntimeException("exculdes.length length must < =1");
		}

		if(!exculdes.contains(datasourceIndex)){
            throw new DragonHARuntimeException("to exclude index:["+exculdes+"] doesn't match "+datasourceIndex);
        }
		return null;
	}
}
