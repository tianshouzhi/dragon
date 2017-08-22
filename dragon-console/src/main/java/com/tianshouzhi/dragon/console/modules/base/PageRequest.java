package com.tianshouzhi.dragon.console.modules.base;

import java.util.Map;

/**
 * Created by tianshouzhi on 2017/6/27.
 */
public class PageRequest {
	private Integer currentPage = 1;

	private Integer pageSize = 10;

	private Map<String, Object> queryParams;

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getOffset() {
		return (currentPage - 1) * pageSize;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Map<String, Object> getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(Map<String, Object> queryParams) {
		this.queryParams = queryParams;
	}
}
