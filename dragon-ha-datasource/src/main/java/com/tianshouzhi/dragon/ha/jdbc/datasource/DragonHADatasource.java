package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSource;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import com.tianshouzhi.dragon.ha.jdbc.datasource.dbselector.DatasourceWrapper;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class DragonHADatasource extends DragonDataSource {

    private HADataSourceManager haDataSourceManager;
//    private List<DatasourceWrapper> datasourceWrapperList=new CopyOnWriteArrayList<DatasourceWrapper>();
    private AtomicBoolean inited=new AtomicBoolean(false);

    public DragonHADatasource() {
        haDataSourceManager=new HADataSourceManager();
    }

    public void init(){
        if(inited.compareAndSet(false,true)){
            haDataSourceManager.rebuild();
        }
    }

    @Override
    public DragonHAConnection getConnection(String username, String password) throws SQLException {
        init();
        return new DragonHAConnection(username,password, haDataSourceManager);
    }

    /**
     * 添加或者更新物理数据源
     * @param dsIndex
     * @param datasourceWrapper
     * @throws SQLException
     */
    public void put(String dsIndex,DatasourceWrapper datasourceWrapper) throws SQLException{
        if(StringUtils.isBlank(dsIndex)||datasourceWrapper==null){
            throw new SQLException("'dsIndex' and 'datasourceWrapper' can't be null or empty");
        }
        getIndexDsMap().put(dsIndex,datasourceWrapper);
    }

    public void remove(String dsIndex) {
          getIndexDsMap().remove(dsIndex);
    }

    public Map<String,DatasourceWrapper> getIndexDsMap() {
        return haDataSourceManager.getIndexDSMap();
    }
    /**
     * 整体替换
     * @param dsIndexMap
     */
    public void setIndexDsMap(Map<String,DatasourceWrapper> dsIndexMap) throws SQLException{
        if(dsIndexMap==null||dsIndexMap.size()==0){
            throw new SQLException("'dsIndexMap' can't be null or empty");
        }
        haDataSourceManager.setIndexDSMap(dsIndexMap);
    }
}
