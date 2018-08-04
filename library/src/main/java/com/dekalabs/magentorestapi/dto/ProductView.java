package com.dekalabs.magentorestapi.dto;

import com.dekalabs.magentorestapi.pojo.AttributeOption;
import com.dekalabs.magentorestapi.pojo.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java8.util.function.IntFunction;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public class ProductView {

    private Product mainProduct;

    private List<CustomAttributeViewDTO.CustomAttributeView> attributes;
    private List<Product> children;

    private Map<String, List<String>> productMap;

    public ProductView() {}

    public ProductView(Product product) {
        this.mainProduct = product;
    }

    public Product getMainProduct() {
        return mainProduct;
    }

    public void setMainProduct(Product mainProduct) {
        this.mainProduct = mainProduct;
    }

    public List<CustomAttributeViewDTO.CustomAttributeView> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<CustomAttributeViewDTO.CustomAttributeView> attributes) {
        this.attributes = attributes;

        StreamSupport.stream(attributes).parallel().forEach(attr -> StreamSupport.stream(attr.getOptions()).parallel().forEach(opt -> opt.setAttributeCode(attr.getCode())));

        fillMap();
    }

    public List<Product> getChildren() {
        return children;
    }

    public void setChildren(List<Product> children) {
        this.children = children;

        fillMap();
    }

    private void fillMap() {
        if(children == null || attributes == null) return;

        productMap = new HashMap<>();

        for(CustomAttributeViewDTO.CustomAttributeView attribute : attributes) {
            fillAttribute(attribute);
        }
    }

    private void fillAttribute(CustomAttributeViewDTO.CustomAttributeView attribute) {

        for(AttributeOption option : attribute.getOptions()) {
            productMap.put(generateQueryString(attribute.getCode(), option.getId()), option.getProducts());
        }
    }

    private String generateQueryString(String attrCode, Long optId) {
        return "code="+attrCode + "&optId="+optId;
    }

    public Product findProductBy(AttributeOption... options) {
        if(options == null) return null;

        final List<String> filteredProducts = new ArrayList<>();

        for(AttributeOption option : options) {
            if(option == null) continue;

            List<String> productIds = productMap.get(generateQueryString(option.getAttributeCode(), option.getId()));

            if(filteredProducts.size() == 0) {
                if(productIds != null) {
                    filteredProducts.addAll(productIds);
                }
            }
            else {
                //Filter by retrieved

                filteredProducts.clear();
                filteredProducts.addAll(StreamSupport.stream(productIds).filter(id -> filteredProducts.contains(id)).collect(Collectors.toList()));
            }
        }

        if(filteredProducts.size() > 0) {
            return getChildrenProductById(Long.valueOf(filteredProducts.get(0)));
        }

        return null;
    }

    public Product getFirstChildrenProductToShow() {
        if(children == null || children.size() == 0) return null;

        if(attributes == null || attributes.size() == 0) return children.get(0);

        AttributeOption[] attrs = new AttributeOption[attributes.size()];

        for(int i = 0; i < attributes.size(); i++) {
            attrs[i] = attributes.get(i).getOptions().get(0);
        }

        return findProductBy(attrs);
    }

    private Product getChildrenProductById(Long id) {
        int indexOf = children.indexOf(new Product(id));

        return indexOf != -1 ? children.get(indexOf) : null;
    }
}
