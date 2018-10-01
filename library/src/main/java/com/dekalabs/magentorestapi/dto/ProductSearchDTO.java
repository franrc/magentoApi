package com.dekalabs.magentorestapi.dto;

import com.dekalabs.magentorestapi.Jackson;
import com.dekalabs.magentorestapi.pojo.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.IOException;
import java.util.List;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public class ProductSearchDTO {

    private List<Product> products;
    private List<Filter> filterList;
    private int categorySize;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @JsonSetter
    @JsonProperty("filters")
    public void setFilters(List<String> filters) {
        if(filters == null) return;

        this.filterList = StreamSupport.stream(filters)
                .parallel()
                .map(f -> {

                    f = f.replace("\\\"", "'").replace("\\\\/", "/").replace("\\\"", "\"");

                    try {

                        return Jackson.DEFAULT_MAPPER.readValue(f, Filter.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return null;
                }).collect(Collectors.toList());
    }

    public List<Filter> getFilterList() {
        return filterList;
    }

    public int getCategorySize() {
        return categorySize;
    }

    public void setCategorySize(int categorySize) {
        this.categorySize = categorySize;
    }
}
