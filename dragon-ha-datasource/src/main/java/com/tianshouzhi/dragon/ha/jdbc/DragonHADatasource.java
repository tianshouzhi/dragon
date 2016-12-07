package com.tianshouzhi.dragon.ha.jdbc;

import com.tianshouzhi.dragon.common.DataSourceAdapter;
import com.tianshouzhi.dragon.ha.dbselector.DBIndex;
import com.tianshouzhi.dragon.ha.dbselector.DatasourceWrapper;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import com.tianshouzhi.dragon.ha.jdbc.connection.HAConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class DragonHADatasource extends DataSourceAdapter{

    private HAConnectionManager HAConnectionManager;
    private List<DatasourceWrapper> datasourceWrapperList=new CopyOnWriteArrayList<DatasourceWrapper>();
    private AtomicBoolean inited=new AtomicBoolean(false);
    public DragonHADatasource(List<DatasourceWrapper> datasourceWrapperList) {
       this.datasourceWrapperList=datasourceWrapperList;
    }

    public void init(){
        if(!inited.get()){
            inited.set(true);
            if(datasourceWrapperList==null||datasourceWrapperList.size()==0){
                throw new IllegalArgumentException("construct parameter 'datasourceWrapperList' can't be empty");
            }
            HAConnectionManager =new HAConnectionManager(datasourceWrapperList);
        }
    }
    @Override
    public Connection getConnection() throws SQLException {
        init();
        return new DragonHAConnection(HAConnectionManager);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        init();
        return new DragonHAConnection(username,password, HAConnectionManager);
    }

    public void add(DatasourceWrapper...newDatasourceWrappers) throws SQLException{
        if(newDatasourceWrappers==null||newDatasourceWrappers.length==0){return;}{
            if(!inited.get()){
                datasourceWrapperList.addAll(Arrays.asList(newDatasourceWrappers));
            }else{
                datasourceWrapperList.addAll(Arrays.asList(newDatasourceWrappers));
                HAConnectionManager.rebuild(datasourceWrapperList);
            }
        }
    }

    public void remove(String...dbIndex){
        if(dbIndex==null||dbIndex.length==0){return;}
        List<DBIndex> dbIndexList=new ArrayList<DBIndex>();
        for (String index : dbIndex) {
            dbIndexList.add(new DBIndex(index));
        }
        if(dbIndexList!=null&&dbIndexList.size()>0){
            datasourceWrapperList.removeAll(dbIndexList);
            HAConnectionManager.rebuild(datasourceWrapperList);
        }
    }
    // 相同dbIndex的更新,.其他的忽略
    public void update(DatasourceWrapper...newDatasourceWrappers){
        for (DatasourceWrapper datasourceWrapper : datasourceWrapperList) {

        }
    }
}
