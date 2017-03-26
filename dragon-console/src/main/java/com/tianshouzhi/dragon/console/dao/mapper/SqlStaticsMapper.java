package com.tianshouzhi.dragon.console.dao.mapper;

import com.tianshouzhi.dragon.sharding.pipeline.handler.statics.SqlExecutionStatics;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by TIANSHOUZHI336 on 2017/3/23.
 */
@Mapper
public interface SqlStaticsMapper {
    @Insert("")
    public void insert();
    @Insert("")
    public void batchInsert();
    public List<SqlExecutionStatics> queryByLogicDatasource(String logicDatasourceName,long offset,long row);
}
