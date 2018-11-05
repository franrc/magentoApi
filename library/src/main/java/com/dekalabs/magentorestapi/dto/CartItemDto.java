package com.dekalabs.magentorestapi.dto;

import com.dekalabs.magentorestapi.pojo.cart.CartItem;

public class CartItemDto {

    private CartItem cartItem;

    public CartItem getCartItem() {
        return cartItem;
    }

    public void setCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
    }

    public CartItemDto(CartItem cartItem) {
        this.cartItem = cartItem;
    }

    public CartItemDto() {
    }
}
