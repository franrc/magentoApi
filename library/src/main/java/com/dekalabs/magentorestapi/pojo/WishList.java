package com.dekalabs.magentorestapi.pojo;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class WishList extends RealmObject {

    @PrimaryKey
    private Long id;

    private RealmList<WishListItem> products;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RealmList<WishListItem> getProducts() {
        return products;
    }

    public void setProducts(RealmList<WishListItem> products) {
        this.products = products;
    }
}
