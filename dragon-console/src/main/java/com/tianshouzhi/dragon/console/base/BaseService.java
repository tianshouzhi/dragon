package com.tianshouzhi.dragon.console.base;

import java.util.List;

/**
 * Created by tianshouzhi on 2017/6/10.
 */
public interface BaseService<T, M extends BaseMapper<T>> {
	public T findById(Long id) throws Exception;

	public List<T> findAll() throws Exception;

	public int deleteById(Long id) throws Exception;

	public int insert(T entity) throws Exception;

	public int update(T entity) throws Exception;

	public List<T> findByIds(Long... ids);

	Page findPage(BaseRequest baseRequest);
}
