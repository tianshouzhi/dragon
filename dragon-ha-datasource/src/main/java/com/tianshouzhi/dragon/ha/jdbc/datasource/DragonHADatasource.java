package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.jdbc.datasource.DataSourceIndex;
import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSource;
import com.tianshouzhi.dragon.ha.dbselector.DatasourceWrapper;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by TIANSHOUZHI336 on 2016/12/2.
 */
public class DragonHADatasource extends DragonDataSource {

    private com.tianshouzhi.dragon.ha.jdbc.datasource.HADataSourceManager HADataSourceManager;
    private List<DatasourceWrapper> datasourceWrapperList=new CopyOnWriteArrayList<DatasourceWrapper>();
    private AtomicBoolean inited=new AtomicBoolean(false);
    public DragonHADatasource(List<DatasourceWrapper> datasourceWrapperList) {
       this.datasourceWrapperList=datasourceWrapperList;
    }

    public void init(){
        if(inited.compareAndSet(false,true)){
            if(datasourceWrapperList==null||datasourceWrapperList.size()==0){
                throw new IllegalArgumentException("construct parameter 'datasourceWrapperList' can't be empty");
            }
            HADataSourceManager =new HADataSourceManager(datasourceWrapperList);
        }
    }

    @Override
    public DragonHAConnection getConnection(String username, String password) throws SQLException {
        init();
        return new DragonHAConnection(username,password, HADataSourceManager);
    }

    public void add(DatasourceWrapper...newDatasourceWrappers) throws SQLException{
        if(newDatasourceWrappers==null||newDatasourceWrappers.length==0){return;}{
            if(!inited.get()){
                datasourceWrapperList.addAll(Arrays.asList(newDatasourceWrappers));
            }else{
                datasourceWrapperList.addAll(Arrays.asList(newDatasourceWrappers));
                HADataSourceManager.rebuild(datasourceWrapperList);
            }
        }
    }

    public void remove(String...dataSourceIndexes){
        if(dataSourceIndexes==null||dataSourceIndexes.length==0){return;}
        List<DataSourceIndex> dataSourceIndexList =new ArrayList<DataSourceIndex>();
        for (String index : dataSourceIndexes) {
            dataSourceIndexList.add(new DataSourceIndex(index));
        }
        if(dataSourceIndexList !=null&& dataSourceIndexList.size()>0){
            datasourceWrapperList.removeAll(dataSourceIndexList);
            HADataSourceManager.rebuild(datasourceWrapperList);
        }
    }
    // 相同dbIndex的更新,.其他的忽略
    public void update(DatasourceWrapper...newDatasourceWrappers){
        for (DatasourceWrapper datasourceWrapper : datasourceWrapperList) {

        }
    }
}
