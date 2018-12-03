package com.dekalabs.magentorestapi.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class WishListItem extends RealmObject {

    @JsonProperty("wishlist_item_id")
    @PrimaryKey
    private Long id;

    @JsonProperty("product_id")
    private Long productId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
