package com.tianshouzhi.dragon.common.mybatis;

import java.util.List;

/**
 * Created by tianshouzhi on 2017/6/11.
 */
public class Page {
    private Long recordsTotal;
    private Long recordsFiltered;
    private PageRequest pageRequest;
    List<?> records;

    public Page(Long recordsTotal, Long recordsFiltered, PageRequest pageRequest, List<?> records) {
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.pageRequest = pageRequest;
        this.records = records;
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

    public PageRequest getPageRequest() {
        return pageRequest;
    }

    public void setPageRequest(PageRequest pageRequest) {
        this.pageRequest = pageRequest;
    }
}
