package com.dekalabs.magentorestapi.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.dekalabs.magentorestapi.Jackson;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class Product extends RealmObject implements Parcelable {

    private Long id;
    private String sku;
    private String name;

    private Long categoryId;

    @JsonProperty("attribute_set_id")
    private int attributeSetId;

    private double price;
    private int status;
    private int visibility;

    @JsonProperty("type_id")
    private String typeId;

    @JsonProperty("custom_attributes")
    private RealmList<ProductAttributes> customAttributes;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("created_at")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updated_at")
    private Date updatedAt;

    private double weight;

    @JsonIgnore
    private ProductStock stock;

    private RealmList<CustomAttribute> configurableAttributes;

//    @JsonProperty("tier_prices")
//    private List<TierPrice> tierPrices;


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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public RealmList<ProductAttributes> getCustomAttributes() {
        return customAttributes;
    }

    public void setCustomAttributes(RealmList<ProductAttributes> customAttributes) {
        this.customAttributes = customAttributes;
    }

    public RealmList<CustomAttribute> getConfigurableAttributes() {
        return configurableAttributes;
    }

    public void setConfigurableAttributes(RealmList<CustomAttribute> configurableAttributes) {
        this.configurableAttributes = configurableAttributes;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("extension_attributes")
    private void unpackExtensionAttrs(Map<String,Object> extension) {

        if(extension != null) {
            Object object = extension.get("stock_item");

            if(object != null) {
                this.stock = (ProductStock) object;
            }

            Object configObject = extension.get("configurable_product_options");
            if(configObject != null && configObject instanceof ArrayList) {
                this.configurableAttributes = new RealmList<>();

                List<Object> data = (ArrayList)configObject;

                for(Object obj : data) {
                    try {
                        LinkedHashMap<String, String> map = (LinkedHashMap)obj;
                        map.put("attribute_code", map.get("code"));
                        map.put("attribute_id", map.get("id"));

                        configurableAttributes.add(Jackson.DEFAULT_MAPPER.readValue(new JSONObject(map).toString(), CustomAttribute.class));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.sku);
        dest.writeString(this.name);
        dest.writeValue(this.categoryId);
        dest.writeInt(this.attributeSetId);
        dest.writeDouble(this.price);
        dest.writeInt(this.status);
        dest.writeInt(this.visibility);
        dest.writeString(this.typeId);
        dest.writeTypedList(this.customAttributes);
        dest.writeTypedList(this.configurableAttributes);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
        dest.writeDouble(this.weight);
        dest.writeParcelable(this.stock, flags);
    }

    public Product() {
    }

    protected Product(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.sku = in.readString();
        this.name = in.readString();
        this.categoryId = (Long) in.readValue(Long.class.getClassLoader());
        this.attributeSetId = in.readInt();
        this.price = in.readDouble();
        this.status = in.readInt();
        this.visibility = in.readInt();
        this.typeId = in.readString();
        this.customAttributes = new RealmList<>();
        this.customAttributes.addAll(in.createTypedArrayList(ProductAttributes.CREATOR));
        this.configurableAttributes = new RealmList<>();
        this.configurableAttributes.addAll(in.createTypedArrayList(CustomAttribute.CREATOR));
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        this.weight = in.readDouble();
        this.stock = in.readParcelable(ProductStock.class.getClassLoader());
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
