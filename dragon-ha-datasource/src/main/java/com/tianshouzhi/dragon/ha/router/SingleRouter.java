package com.tianshouzhi.dragon.ha.router;

import com.tianshouzhi.dragon.ha.exception.DragonHARuntimeException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by tianshouzhi on 2017/8/16.
 */
public class SingleRouter implements Router {
	private String datasourceIndex;

	public SingleRouter(String datasourceIndex) {
		if (StringUtils.isBlank(datasourceIndex)) {
			throw new DragonHARuntimeException("datasourceIndex can't be blank!");
		}
		this.datasourceIndex = datasourceIndex;
	}

	@Override
	public String route(String... exculdes) {
		if (exculdes.length > 1) {
			throw new DragonHARuntimeException("exculdes.length length must < =1");
		}
		if (ArrayUtils.isEmpty(exculdes)) {
			return datasourceIndex;
		}

		if(exculdes[0].equals(datasourceIndex)){
            throw new DragonHARuntimeException("to exclude datasourceIndex:["+exculdes[0]+"] doesn't match "+datasourceIndex);
        }
		return null;
	}
}
