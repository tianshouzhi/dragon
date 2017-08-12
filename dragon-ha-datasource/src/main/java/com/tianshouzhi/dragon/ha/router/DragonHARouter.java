package com.tianshouzhi.dragon.ha.router;

import com.tianshouzhi.dragon.ha.jdbc.datasource.RealDataSourceWrapperManager;
import com.tianshouzhi.dragon.ha.router.weight.DBSelector;
import com.tianshouzhi.dragon.ha.router.weight.ReadDBSelector;
import com.tianshouzhi.dragon.ha.router.weight.WriteDBSelector;

import java.util.Set;

/**
 * Created by tianshouzhi on 2017/8/12.
 */
public class DragonHARouter {

    private RealDataSourceWrapperManager dataSourceWrapperManager;


    public DragonHARouter(RealDataSourceWrapperManager dataSourceWrapperManager) {
        this.dataSourceWrapperManager = dataSourceWrapperManager;
    }
}
