package com.dekalabs.magentorestapi.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Product {

    private Long id;
    private String sku;
    private String name;

    @JsonProperty("attribute_set_id")
    private int attributeSetId;

    private double price;
    private int status;
    private int visibility;

    @JsonProperty("type_id")
    private String typeId;

    @JsonIgnore
    private String description;

    @JsonIgnore
    private String thumbnail;

    @JsonIgnore
    private String image;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("created_at")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updated_at")
    private Date updatedAt;

    private double weight;

    @JsonIgnore
    private ProductStock stock;

    @JsonProperty("tier_prices")
    private List<TierPrice> tierPrices;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAttributeSetId() {
        return attributeSetId;
    }

    public void setAttributeSetId(int attributeSetId) {
        this.attributeSetId = attributeSetId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public ProductStock getStock() {
        return stock;
    }

    public void setStock(ProductStock stock) {
        this.stock = stock;
    }

    public List<TierPrice> getTierPrices() {
        return tierPrices;
    }

    public void setTierPrices(List<TierPrice> tierPrices) {
        this.tierPrices = tierPrices;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("extension_attributes")
    private void unpackStock(Map<String,Object> extension) {

        if(extension != null) {
            Object object = extension.get("stock_item");

            if(object != null) {
                this.stock = (ProductStock) object;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("custom_attributes")
    private void unpackCustomAttributes(List<Map<String,Object>> customAttrs) {

        if(customAttrs != null) {

            for(Map<String, Object> attr : customAttrs) {

                String name = attr.get("attribute_code").toString();

                if (name.equals("description")) {
                    this.description = attr.get("value").toString();
                } else if (name.equals("thumbnail")) {
                    this.thumbnail = attr.get("value").toString();
                } else if (name.equals("image")) {
                    this.image = attr.get("value").toString();
                }
            }
        }
    }
}
