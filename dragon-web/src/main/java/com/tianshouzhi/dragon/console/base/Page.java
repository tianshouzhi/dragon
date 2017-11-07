package com.tianshouzhi.dragon.console.base;

import java.util.List;

/**
 * Created by tianshouzhi on 2017/6/27.
 */
public class Page {
	private Long recordsTotal;

	private Long recordsFiltered;

	private List<?> records;

	private BaseRequest request;

	public Page(Long recordsTotal, Long recordsFiltered, List<?> records, BaseRequest request) {
		this.recordsTotal = recordsTotal;
		this.recordsFiltered = recordsFiltered;
		this.records = records;
		this.request = request;
	}

	public BaseRequest getRequest() {
		return request;
	}

	public void setRequest(BaseRequest request) {
		this.request = request;
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
