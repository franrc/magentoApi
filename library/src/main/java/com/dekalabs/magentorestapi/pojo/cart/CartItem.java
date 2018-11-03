package com.dekalabs.magentorestapi.pojo.cart;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItem implements Parcelable {

    @JsonProperty("item_id")
    public Integer itemId;

    public String sku;

    public Integer qty;

    public String name;

    public Integer price;
    @JsonProperty("product_type")

    public String productType;
    @JsonProperty("quote_id")
    public String quoteId;


    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
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
        return price;
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
    }

    public CartItem() {
    }

    protected CartItem(Parcel in) {
        this.itemId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.sku = in.readString();
        this.qty = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.price = (Integer) in.readValue(Integer.class.getClassLoader());
        this.productType = in.readString();
        this.quoteId = in.readString();
    }

    public static final Parcelable.Creator<CartItem> CREATOR = new Parcelable.Creator<CartItem>() {
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