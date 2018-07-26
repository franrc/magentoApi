package com.dekalabs.magentorestapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class MagentoListResponse<DATA> {

    private List<DATA> items;

    @JsonIgnore
    private int pageSize;

    @JsonIgnore
    private int currentPage;

    @JsonProperty("total_count")
    private int totalCount;

    @JsonIgnore
    private MagentoError error;


    public List<DATA> getItems() {
        return items;
    }

    public void setItems(List<DATA> items) {
        this.items = items;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public MagentoError getError() {
        return error;
    }

    public void setError(MagentoError error) {
        this.error = error;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("search_criteria")
    private void unpackSearchCriteria(Map<String,Object> criteria) {

        if(criteria != null) {
            Object pageSize = criteria.get("page_size");

            if(pageSize != null)
                this.pageSize = Integer.parseInt(pageSize.toString());

            Object currentPage = criteria.get("current_page");

            if(currentPage != null)
                this.currentPage = Integer.parseInt(currentPage.toString());
        }
    }
}
