package com.dekalabs.magentorestapi.dto;

import com.dekalabs.magentorestapi.pojo.Product;

import java.util.List;

public class ProductSearchDTO {

    private List<Product> products;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
