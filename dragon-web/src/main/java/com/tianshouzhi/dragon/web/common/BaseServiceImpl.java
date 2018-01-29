package com.tianshouzhi.dragon.web.common;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by tianshouzhi on 2017/6/10.
 */
public abstract class BaseServiceImpl<T, M extends BaseMapper<T>>
      implements BaseService<T, M>, ApplicationContextAware {
	protected M mapper;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Type type = this.getClass().getGenericSuperclass();
		Type trueType = ((ParameterizedType) type).getActualTypeArguments()[1];
		mapper = (M) applicationContext.getBean((Class<Object>) trueType);
	}

	@Override
	public Page findPage(BaseRequest baseRequest) {
		List<?> records = mapper.findByCondition(baseRequest);
		Long recordsTotal = mapper.selectCount();
		Long recordsFiltered = mapper.selectCountByCondition(baseRequest);
		return new Page(recordsTotal, recordsFiltered, records, baseRequest);
	}

	@Override
	public T findById(Long id) throws Exception {
		return mapper.findById(id);
	}

	@Override
	public List<T> findAll() throws Exception {
		return mapper.findAll();
	}

	@Override
	public int deleteById(Long id) throws Exception {
		return mapper.deleteById(id);
	}

	@Override
	public int insert(T entity) throws Exception {
		return mapper.insert(entity);
	}

	@Override
	public int update(T entity) throws Exception {
		return mapper.update(entity);
	}

	@Override
	public List<T> findByIds(Long... ids) {
		return mapper.findByIds(ids);
	}

}
