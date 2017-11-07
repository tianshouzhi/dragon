package com.tianshouzhi.dragon.console.base;

import java.util.List;

/**
 * Created by tianshouzhi on 2017/6/10.
 */
public interface BaseMapper<T> {
	public T findById(Long id);

	public List<T> findAll();

	public int deleteById(Long id);

	public int insert(T entity);

	public int update(T entity);

	public List<T> findByIds(Long... ids);

	Long selectCount();

	Long selectCountByCondition(BaseRequest baseRequest);

	List<?> findByCondition(BaseRequest baseRequest);
}
