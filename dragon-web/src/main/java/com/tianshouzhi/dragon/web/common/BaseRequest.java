package com.tianshouzhi.dragon.web.common;

/**
 * Created by tianshouzhi on 2017/6/27.
 */
public class BaseRequest {
	private Integer currentPage = 1;

	private Integer pageSize = 10;

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
}
