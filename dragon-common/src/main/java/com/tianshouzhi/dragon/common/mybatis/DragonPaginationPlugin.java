package com.tianshouzhi.dragon.common.mybatis;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Properties;

/**
 * Created by tianshouzhi on 2017/6/17.
 */
@Intercepts(@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
        RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}))
public class DragonPaginationPlugin implements Interceptor{
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object parameter=invocation.getArgs()[1];
        if(parameter==null||!(parameter instanceof PageRequest)){
            return invocation.proceed();
        }
        PageRequest pageRequest= (PageRequest) parameter;
        List<?> records=queryRecords(pageRequest,invocation);
        Long recordsTotal=queryRecordsTotal(pageRequest,invocation);
        Long recordsFiltered=queryRecordsFiltered(pageRequest,invocation);
        return new Page(recordsTotal,recordsFiltered,pageRequest,records);
    }

    private List<?> queryRecords(PageRequest pageRequest, Invocation invocation) {
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        BoundSql boundSql = ms.getBoundSql(invocation.getArgs());
        String sql = boundSql.getSql();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        Object parameterObject = boundSql.getParameterObject();

        return null;
    }

    private Long queryRecordsTotal(PageRequest pageRequest, Invocation invocation) {
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        return null;
    }

    private Long queryRecordsFiltered(PageRequest pageRequest, Invocation invocation) {
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        return null;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target,this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
