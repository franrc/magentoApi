package com.dekalabs.magentorestapi.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.dekalabs.magentorestapi.Jackson;
import com.dekalabs.magentorestapi.config.MagentoRestConfiguration;
import com.dekalabs.magentorestapi.utils.MagentoDatabaseUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Product extends RealmObject implements Parcelable {

    public static final String TYPE_CONFIGURABLE = "configurable";
    public static final String TYPE_SIMPLE = "simple";

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

    //@JsonProperty("custom_attributes")
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

    @JsonProperty("final_price")
    private double finalPrice;

    private RealmList<CustomAttribute> configurableAttributes;

    private int subProductsCount;

//    @JsonProperty("tier_prices")
//    private List<TierPrice> tierPrices;

    private boolean favourite;


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

    public int getPrice() {
        if(finalPrice > 0) return (int)Math.round(finalPrice);

        return (int)Math.round(price);
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

    public double getFinalPrice() {
        return (int)Math.round(finalPrice);
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public int getSubProductsCount() {
        return subProductsCount;
    }

    public void setSubProductsCount(int subProductsCount) {
        this.subProductsCount = subProductsCount;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("extension_attributes")
    private void unpackExtensionAttrs(Map<String,Object> extension) {

        if(extension != null) {
            Object object = extension.get("stock_item");

            if(object != null) {
                try {
                    this.stock = Jackson.DEFAULT_MAPPER.readValue(new JSONObject((LinkedHashMap<String, Object>)object).toString(), ProductStock.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Object childrenCountObj = extension.get("children_count");

            if(childrenCountObj != null) {
                this.subProductsCount = (int)childrenCountObj;
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

    @SuppressWarnings("unchecked")
    @JsonProperty("custom_attributes")
    private void unpackCustomAttrs(List<Map<String,Object>> customAttrs) {

        if(customAttrs == null) return;

        customAttributes = new RealmList<>();

        for(Map<String, Object> attr : customAttrs) {

            String code = (String)attr.get("attribute_code");

            Object value  = attr.get("value");

            MagentoDatabaseUtils database = new MagentoDatabaseUtils();

            if(value instanceof String) {
                ProductAttributes productAttributes = new ProductAttributes();
                productAttributes.setAttribute(code);
                productAttributes.generatePrimaryKey(id);
                productAttributes.setName(attr.get("name").toString());
                productAttributes.setValue(new RealmList<>());

                AttributeOption option = database.findAttributeByValue(code, value.toString());

                if(option != null) {
                    productAttributes.getValues().add(option.getLabel());
                }
                else {
                    productAttributes.getValues().add(value.toString());
                }

                customAttributes.add(productAttributes);
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
        dest.writeDouble(finalPrice);
        dest.writeInt(subProductsCount);
        dest.writeInt(favourite ? 1 : 0);
    }

    public Product() {
    }

    public Product(Long id) {
        this.id = id;
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

        List<ProductAttributes> productAttributes = in.createTypedArrayList(ProductAttributes.CREATOR);
        if(productAttributes != null) {
            this.customAttributes = new RealmList<>();
            this.customAttributes.addAll(productAttributes);
        }

        List<CustomAttribute> customAttributes = in.createTypedArrayList(CustomAttribute.CREATOR);
        if(customAttributes != null) {
            this.configurableAttributes = new RealmList<>();
            this.configurableAttributes.addAll(customAttributes);
        }

        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        this.weight = in.readDouble();
        this.stock = in.readParcelable(ProductStock.class.getClassLoader());
        this.finalPrice = in.readDouble();
        this.subProductsCount = in.readInt();
        this.favourite = in.readInt() == 1;
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


    public String getDescription() {
        return getCustomAttribute("description");
    }

    public String getImage() {
        String image = getCustomAttribute("image");
        if(image != null && !image.startsWith("http")) {
            return MagentoRestConfiguration.getProductMediaUrlPath() + image;
        }

        return null;
    }

    public String getThumbnail() {
        String image =  getCustomAttribute("thumbnail");
        if(image != null && !image.startsWith("http")) {
            return MagentoRestConfiguration.getProductMediaUrlPath() + image;
        }

        return null;
    }

    public String getSmallImage() {
        String image =  getCustomAttribute("small_image");
        if(image != null && !image.startsWith("http")) {
            return MagentoRestConfiguration.getProductMediaUrlPath() + image;
        }

        return null;
    }


    public String getCustomAttribute(String code) {

        if(customAttributes == null) return null;

        int indexOf = customAttributes.indexOf(new ProductAttributes(code));

        if(indexOf == -1) return null;
//
        ProductAttributes attribute = customAttributes.get(indexOf);

        return attribute.getValue();
    }

    private List<String> getCustomAttributeList(String code) {
        if(customAttributes == null) return null;

        return customAttributes.first(new ProductAttributes(code)).getValues();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        return id != null ? id.equals(product.id) : product.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
