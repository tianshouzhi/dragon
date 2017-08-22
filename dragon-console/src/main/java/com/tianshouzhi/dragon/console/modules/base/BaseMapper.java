package com.tianshouzhi.dragon.console.modules.base;

import java.util.List;

/**
 * Created by tianshouzhi on 2017/8/21.
 */
public interface BaseMapper<T> {
	public int insert(T entity);

	public int batchInsert(List<T> entities);

	public int update(T entity);

	public int batchUpdate(List<T> entities);

	public int delete(Long id);

	public int batchDelete(List<Long> ids);

	public T find(Long id);

	public List<T> findPage();

	Long selectCount();

	Long selectCountByCondition(PageRequest pageRequest);

	List<?> findByCondition(PageRequest pageRequest);
}
