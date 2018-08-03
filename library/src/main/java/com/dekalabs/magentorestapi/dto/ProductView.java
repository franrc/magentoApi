package com.dekalabs.magentorestapi.dto;

import com.dekalabs.magentorestapi.pojo.AttributeOption;
import com.dekalabs.magentorestapi.pojo.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        this.attributes.get(0).getOptions().get(1).getProducts().add( 1, children.get(6).getId().toString());
        this.attributes.get(0).getOptions().get(2).getProducts().add(1,children.get(7).getId().toString());
        this.attributes.get(0).getOptions().get(3).getProducts().add(1, children.get(8).getId().toString());

        CustomAttributeViewDTO.CustomAttributeView attrNew = new CustomAttributeViewDTO.CustomAttributeView();
        attrNew.setCode("wjh_size");
        attrNew.setId(123123L);
        attrNew.setLabel("Size");

        AttributeOption op1 =  new AttributeOption();
        op1.setValue("15ml");
        op1.setId(12312313L);
        op1.setLabel("15ml");
        op1.setProducts(Arrays.asList(children.get(6).getId().toString()));

        AttributeOption op2 =  new AttributeOption();
        op2.setValue("30ml");
        op2.setId(12312314L);
        op2.setLabel("30ml");
        op2.setProducts(Arrays.asList(children.get(7).getId().toString()));

        AttributeOption op3 =  new AttributeOption();
        op3.setValue("45ml");
        op3.setId(12312315L);
        op3.setLabel("45ml");
        op3.setProducts(Arrays.asList(children.get(8).getId().toString()));

        attrNew.setOptions(Arrays.asList(op1, op2, op3));

        this.attributes.add(attrNew);



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

    private Product getChildrenProductById(Long id) {
        int indexOf = children.indexOf(new Product(id));

        return indexOf != -1 ? children.get(indexOf) : null;
    }
}
