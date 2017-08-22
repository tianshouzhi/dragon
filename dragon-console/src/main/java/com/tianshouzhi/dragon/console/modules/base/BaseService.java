package com.tianshouzhi.dragon.console.modules.base;

import java.util.List;

/**
 * Created by tianshouzhi on 2017/8/21.
 */
public interface BaseService<T extends BaseEntity, M extends BaseMapper<T>> {
	public int insert(T entity);

	public int batchInsert(List<T> entities);

	public int update(T entity);

	public int batchUpdate(List<T> entities);

	public int delete(Long id);

	public int batchDelete(List<Long> ids);

	public T find(Long id);

	public Page findPage(PageRequest pageRequest);
}
