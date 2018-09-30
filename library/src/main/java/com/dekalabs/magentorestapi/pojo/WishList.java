package com.dekalabs.magentorestapi.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class WishList extends RealmObject implements Parcelable {

    @PrimaryKey
    private Long id;

    private RealmList<String> products = new RealmList<>();

    @Ignore
    private List<Product> productList = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(RealmList<String> products) {
        this.products = products;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeList(this.products);
    }

    public WishList() {
        products = new RealmList<>();
    }

    protected WishList(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.products = new RealmList<>();
        in.readList(this.products, Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<WishList> CREATOR = new Parcelable.Creator<WishList>() {
        @Override
        public WishList createFromParcel(Parcel source) {
            return new WishList(source);
        }

        @Override
        public WishList[] newArray(int size) {
            return new WishList[size];
        }
    };
}
