package com.tianshouzhi.dragon.console.dao.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * Created by TIANSHOUZHI336 on 2017/3/23.
 */
@Mapper
public interface LogicDatasourceMapper {
    public void batchInsert();
    public void insert();
}
