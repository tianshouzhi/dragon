package com.tianshouzhi.dragon.console.modules.base;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by tianshouzhi on 2017/8/21.
 */
public abstract class BaseServiceImpl<T extends BaseEntity, M extends BaseMapper<T>>
      implements BaseService<T, M>, ApplicationContextAware {

	protected M mapper;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Type type = this.getClass().getGenericSuperclass();
		Type trueType = ((ParameterizedType) type).getActualTypeArguments()[1];
		mapper = (M) applicationContext.getBean((Class<Object>) trueType);
	}

	private ApplicationContext applicationContext;

	@Override
	public int insert(T entity) {
		return mapper.insert(entity);
	}

	@Override
	public int batchInsert(List<T> entities) {
		return mapper.batchInsert(entities);
	}

	@Override
	public int update(T entity) {
		return mapper.update(entity);
	}

	@Override
	public int batchUpdate(List<T> entities) {
		return mapper.batchUpdate(entities);
	}

	@Override
	public int delete(Long id) {
		return mapper.delete(id);
	}

	@Override
	public int batchDelete(List<Long> ids) {
		return mapper.batchDelete(ids);
	}

	@Override
	public T find(Long id) {
		return mapper.find(id);
	}

	@Override
	public Page findPage(PageRequest pageRequest) {
		List<?> records = mapper.findByCondition(pageRequest);
		Long recordsTotal = mapper.selectCount();
		Long recordsFiltered = mapper.selectCountByCondition(pageRequest);
		return new Page(recordsTotal, recordsFiltered, records, pageRequest);
	}

}
