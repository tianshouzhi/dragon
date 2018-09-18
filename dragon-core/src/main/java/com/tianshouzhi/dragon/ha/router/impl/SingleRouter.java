package com.tianshouzhi.dragon.ha.router.impl;

import com.tianshouzhi.dragon.common.util.CollectionUtils;
import com.tianshouzhi.dragon.common.util.StringUtils;
import com.tianshouzhi.dragon.ha.exception.HASQLException;

import java.util.Set;

/**
 * Created by tianshouzhi on 2017/8/16.
 */
public class SingleRouter extends BaseRouter {
    private String datasourceIndex;

    public SingleRouter(String haDataSourceName,String realDataSourceName) {
        super(haDataSourceName);
        if (StringUtils.isBlank(realDataSourceName)) {
            throw new HASQLException("index can't be blank!");
        }
        this.datasourceIndex = realDataSourceName;
    }

    @Override
    public String doRoute(Set<String> excludes) {
        if (CollectionUtils.isEmpty(excludes)) {
            return datasourceIndex;
        }
        throw new HASQLException("real datasource "+ datasourceIndex+" is not available!!!");
    }
}
