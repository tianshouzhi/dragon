package com.tianshouzhi.site.domain.dto;

import java.util.List;

/**
 * Created by tianshouzhi on 2017/6/11.
 */
public class Page<T> {
    private Long recordsTotal;
    private Long recordsFiltered;
    private PageRequest pageRequest;
    List<T> data;

    public Page(Long recordsTotal, Long recordsFiltered, PageRequest pageRequest, List<T> data) {
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.pageRequest = pageRequest;
        this.data = data;
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

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public PageRequest getPageRequest() {
        return pageRequest;
    }

    public void setPageRequest(PageRequest pageRequest) {
        this.pageRequest = pageRequest;
    }
}
