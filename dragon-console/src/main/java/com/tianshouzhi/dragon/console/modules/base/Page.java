package com.tianshouzhi.dragon.console.modules.base;

import java.util.List;

/**
 * Created by tianshouzhi on 2017/6/27.
 */
public class Page {
	private Long recordsTotal;

	private Long recordsFiltered;

	private List<?> records;

	private PageRequest pageRequest;

	public Page(Long recordsTotal, Long recordsFiltered, List<?> records, PageRequest pageRequest) {
		this.recordsTotal = recordsTotal;
		this.recordsFiltered = recordsFiltered;
		this.records = records;
		this.pageRequest = pageRequest;
	}

	public PageRequest getPageRequest() {
		return pageRequest;
	}

	public void setPageRequest(PageRequest pageRequest) {
		this.pageRequest = pageRequest;
	}

	public Long getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(Long recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public Long getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(Long recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

	public List<?> getRecords() {
		return records;
	}

	public void setRecords(List<?> records) {
		this.records = records;
	}
}
