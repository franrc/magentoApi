
package com.dekalabs.magentorestapi.pojo.cart;

import android.os.Parcel;
import android.os.Parcelable;

import com.dekalabs.magentorestapi.pojo.Address;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingCart extends RealmObject implements Parcelable {

    @PrimaryKey
    private Long id;

    @JsonIgnore
    private String cartIdentifier;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;


    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updatedAt;

    @JsonProperty("is_active")
    private Boolean isActive;

    private RealmList<CartItem> items;

    @JsonProperty("items_count")
    private Integer itemsCount;
    @JsonProperty("items_qty")
    private Integer itemsQty;

    @JsonProperty("billing_address")
    private Address billingAddress;

    @JsonProperty("store_id")
    private Integer storeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public RealmList<CartItem> getItems() {
        return items;
    }

    public void setItems(RealmList<CartItem> items) {
        this.items = items;
    }

    public Integer getItemsCount() {
        return itemsCount;
    }

    public void setItemsCount(Integer itemsCount) {
        this.itemsCount = itemsCount;
    }

    public Integer getItemsQty() {
        return itemsQty;
    }

    public void setItemsQty(Integer itemsQty) {
        this.itemsQty = itemsQty;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getCartIdentifier() {
        return cartIdentifier;
    }

    public void setCartIdentifier(String cartIdentifier) {
        this.cartIdentifier = cartIdentifier;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.cartIdentifier);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeString(this.updatedAt);
        dest.writeValue(this.isActive);
        dest.writeList(this.items);
        dest.writeValue(this.itemsCount);
        dest.writeValue(this.itemsQty);
        dest.writeParcelable(this.billingAddress, flags);
        dest.writeValue(this.storeId);
    }

    public ShoppingCart() {
    }

    protected ShoppingCart(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.cartIdentifier = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        this.updatedAt = in.readString();
        this.isActive = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.items = new RealmList<>();
        in.readList(this.items, CartItem.class.getClassLoader());
        this.itemsCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.itemsQty = (Integer) in.readValue(Integer.class.getClassLoader());
        this.billingAddress = in.readParcelable(Address.class.getClassLoader());
        this.storeId = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<ShoppingCart> CREATOR = new Parcelable.Creator<ShoppingCart>() {
        @Override
        public ShoppingCart createFromParcel(Parcel source) {
            return new ShoppingCart(source);
        }

        @Override
        public ShoppingCart[] newArray(int size) {
            return new ShoppingCart[size];
        }
    };
}
