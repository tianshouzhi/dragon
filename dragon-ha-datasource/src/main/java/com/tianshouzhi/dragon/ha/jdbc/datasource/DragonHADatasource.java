package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.initailzer.DataSourceInitailzer;
import com.tianshouzhi.dragon.common.initailzer.DataSourceInitailzerUtil;
import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSource;
import com.tianshouzhi.dragon.ha.config.DatasourceConfig;
import com.tianshouzhi.dragon.ha.config.DragonHAConfiguration;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import com.tianshouzhi.dragon.ha.jdbc.datasource.dbselector.DatasourceWrapper;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class DragonHADatasource extends DragonDataSource {
    private HADataSourceManager haDataSourceManager;

    public DragonHADatasource(DragonHAConfiguration configuration) throws Exception {
        haDataSourceManager=new HADataSourceManager();
        List<DatasourceConfig> dsConfigList = configuration.getDsConfigList();
        HashMap<String, DatasourceWrapper> indexDSMap = new HashMap<String, DatasourceWrapper>();
        for (DatasourceConfig datasourceConfig : dsConfigList) {
            String realClass = datasourceConfig.getRealClass();
            int readWeight = datasourceConfig.getReadWeight();
            Integer writeWeight = datasourceConfig.getWriteWeight();
            String index = datasourceConfig.getIndex();
            List<DatasourceConfig.Property> properties = datasourceConfig.getProperties();
            DataSource dataSource = DataSourceInitailzerUtil.init(realClass, DatasourceConfig.propertiesToMap(properties));
            indexDSMap.put(index,new DatasourceWrapper(index,readWeight,writeWeight,dataSource));
        }
        haDataSourceManager.setIndexDSMap(indexDSMap);
        haDataSourceManager.rebuild();
    }


    @Override
    public DragonHAConnection getConnection(String username, String password) throws SQLException {
        return new DragonHAConnection(username,password, haDataSourceManager);
    }
}
