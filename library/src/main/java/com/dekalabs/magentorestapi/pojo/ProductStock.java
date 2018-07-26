package com.dekalabs.magentorestapi.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.realm.RealmObject;

public class ProductStock extends RealmObject implements Parcelable {

    @JsonProperty("item_id")
    private Long itemId;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("stock_id")
    private Long stockId;

    private Long qty;

    @JsonProperty("is_in_stock")
    private boolean isInStock;


    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }

    public boolean isInStock() {
        return isInStock;
    }

    public void setInStock(boolean inStock) {
        isInStock = inStock;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.itemId);
        dest.writeValue(this.productId);
        dest.writeValue(this.stockId);
        dest.writeValue(this.qty);
        dest.writeByte(this.isInStock ? (byte) 1 : (byte) 0);
    }

    public ProductStock() {
    }

    protected ProductStock(Parcel in) {
        this.itemId = (Long) in.readValue(Long.class.getClassLoader());
        this.productId = (Long) in.readValue(Long.class.getClassLoader());
        this.stockId = (Long) in.readValue(Long.class.getClassLoader());
        this.qty = (Long) in.readValue(Long.class.getClassLoader());
        this.isInStock = in.readByte() != 0;
    }

    public static final Parcelable.Creator<ProductStock> CREATOR = new Parcelable.Creator<ProductStock>() {
        @Override
        public ProductStock createFromParcel(Parcel source) {
            return new ProductStock(source);
        }

        @Override
        public ProductStock[] newArray(int size) {
            return new ProductStock[size];
        }
    };
}
