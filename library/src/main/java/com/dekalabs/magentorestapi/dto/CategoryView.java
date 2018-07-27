package com.dekalabs.magentorestapi.dto;

import com.dekalabs.magentorestapi.pojo.Product;

import java.util.List;

public class CategoryView {

    private Navigation navigation;

    public Navigation getNavigation() {
        return navigation;
    }

    public void setNavigation(Navigation navigation) {
        this.navigation = navigation;
    }

    public List<Product> getProductList() {
        return navigation != null ? navigation.products : null;
    }


    public static class Navigation {
        private List<Product> products;

        public List<Product> getProducts() {
            return products;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }
    }
}
