package com.dekalabs.magentorestapi.pojo.cart;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItem extends RealmObject implements Parcelable {

    @PrimaryKey
    @JsonProperty("item_id")
    public Long itemId;

    public String sku;

    public Integer qty;

    public String name;

    public Integer price;
    @JsonProperty("product_type")

    public String productType;
    @JsonProperty("quote_id")
    public String quoteId;

    @JsonIgnore
    private String image;

    @JsonIgnore
    private String brand;


    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return (int)Math.round(price);
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CartItem cartItem = (CartItem) o;

        return itemId != null ? itemId.equals(cartItem.itemId) : cartItem.itemId == null;
    }

    @Override
    public int hashCode() {
        return itemId != null ? itemId.hashCode() : 0;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.itemId);
        dest.writeString(this.sku);
        dest.writeValue(this.qty);
        dest.writeString(this.name);
        dest.writeValue(this.price);
        dest.writeString(this.productType);
        dest.writeString(this.quoteId);
        dest.writeString(this.image);
        dest.writeString(this.brand);
    }

    public CartItem() {
    }

    protected CartItem(Parcel in) {
        this.itemId = (Long) in.readValue(Long.class.getClassLoader());
        this.sku = in.readString();
        this.qty = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.price = (Integer) in.readValue(Integer.class.getClassLoader());
        this.productType = in.readString();
        this.quoteId = in.readString();
        this.image = in.readString();
        this.brand = in.readString();
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel source) {
            return new CartItem(source);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };
}