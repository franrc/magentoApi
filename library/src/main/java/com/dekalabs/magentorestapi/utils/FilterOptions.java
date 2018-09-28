package com.dekalabs.magentorestapi.utils;

import com.dekalabs.magentorestapi.dto.Filter;
import com.dekalabs.magentorestapi.dto.Pagination;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterOptions {

    public static String EQUALS = "eq";
    public static String LIKE = "like";
    public static String IN = "in";

    public enum SORT_DIRECTION { ASC, DESC }

    Map<String, String> filterMap;
    int andValue = 0;
    int orValue = 0;

    int sortValue = 0;

    boolean canAdd;

    public FilterOptions() {
        filterMap = new HashMap<>();
        canAdd = true;
    }


    public FilterOptions addFilter(String name, String value, String conditionType) {
        if(! canAdd) {
            throw new IllegalStateException("You must call and() or or() before add any filter");
        }

        filterMap.put("searchCriteria[filterGroups]["+ andValue + "][filters]["+ orValue +"][field]", name);
        filterMap.put("searchCriteria[filterGroups]["+ andValue + "][filters]["+ orValue +"][value]", value);
        filterMap.put("searchCriteria[filterGroups]["+ andValue  + "][filters]["+ orValue +"][conditionType]", conditionType    );

        canAdd = false;

        return this;
    }

    public FilterOptions applyClientFilter(List<Filter> filters) {
        if(filters == null) return this;

        if(! canAdd) {
            throw new IllegalStateException("You must call and() or or() before add any filter");
        }

        for(Filter filter : filters) {

            String commaSeparatedValues = filter.getCommaSeparatedValues();

            if(commaSeparatedValues == null) continue;

            filterMap.put("searchCriteria[filterGroups][" + andValue + "][filters][" + orValue + "][field]", filter.getFilterCode());
            filterMap.put("searchCriteria[filterGroups][" + andValue + "][filters][" + orValue + "][value]", commaSeparatedValues);
            filterMap.put("searchCriteria[filterGroups][" + andValue + "][filters][" + orValue + "][conditionType]", "in");

            or();
        }

        canAdd = false;

        return this;
    }

    public FilterOptions sort(String name, SORT_DIRECTION direction) {
        filterMap.put("searchCriteria[sortOrders]["+ sortValue + "][field]", name);
        filterMap.put("searchCriteria[sortOrders]["+ sortValue + "][direction]", direction.name());

        sortValue++;

        return this;
    }

    public FilterOptions and() {
        andValue++;
        orValue = 0;

        canAdd = true;

        return this;
    }

    public FilterOptions or() {
        orValue++;

        canAdd = true;

        return this;
    }

    public FilterOptions showFields(String fields) {
        filterMap.put("fields", fields);
        return this;
    }

    public FilterOptions addPagination(Pagination pagination) {
        if(pagination != null) {
            filterMap.put("searchCriteria[pageSize]", String.valueOf(pagination.getPageSize()));
            filterMap.put("searchCriteria[currentPage]", String.valueOf(pagination.getCurrentPage()));
        }

        return this;
    }

    public Map<String, String> build() {
        return filterMap;
    }

}
