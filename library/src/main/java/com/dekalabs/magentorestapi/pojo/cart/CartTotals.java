package com.dekalabs.magentorestapi.pojo.cart;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.realm.RealmObject;

public class CartTotals extends RealmObject implements Parcelable {

    @JsonProperty("base_grand_total")
    private double total;

    @JsonProperty("base_subtotal")
    private double subtotal;

    @JsonProperty("base_discount_amount")
    private double discount;

    @JsonProperty("base_shipping_amount")
    private double shippingAmount;

    @JsonProperty("base_shipping_discount_amount")
    private double shippingDiscount;

    @JsonProperty("base_tax_amount")
    private double taxAmount;

    @JsonProperty("base_shipping_tax_amount")
    private double shippingTax;

    @JsonProperty("base_shipping_incl_tax")
    private double shippingIncludingTax;

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getShippingAmount() {
        return shippingAmount;
    }

    public void setShippingAmount(double shippingAmount) {
        this.shippingAmount = shippingAmount;
    }

    public double getShippingDiscount() {
        return shippingDiscount;
    }

    public void setShippingDiscount(double shippingDiscount) {
        this.shippingDiscount = shippingDiscount;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getShippingTax() {
        return shippingTax;
    }

    public void setShippingTax(double shippingTax) {
        this.shippingTax = shippingTax;
    }

    public double getShippingIncludingTax() {
        return shippingIncludingTax;
    }

    public void setShippingIncludingTax(double shippingIncludingTax) {
        this.shippingIncludingTax = shippingIncludingTax;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.total);
        dest.writeDouble(this.subtotal);
        dest.writeDouble(this.discount);
        dest.writeDouble(this.shippingAmount);
        dest.writeDouble(this.shippingDiscount);
        dest.writeDouble(this.taxAmount);
        dest.writeDouble(this.shippingTax);
        dest.writeDouble(this.shippingIncludingTax);
    }

    public CartTotals() {
    }

    protected CartTotals(Parcel in) {
        this.total = in.readDouble();
        this.subtotal = in.readDouble();
        this.discount = in.readDouble();
        this.shippingAmount = in.readDouble();
        this.shippingDiscount = in.readDouble();
        this.taxAmount = in.readDouble();
        this.shippingTax = in.readDouble();
        this.shippingIncludingTax = in.readDouble();
    }

    public static final Parcelable.Creator<CartTotals> CREATOR = new Parcelable.Creator<CartTotals>() {
        @Override
        public CartTotals createFromParcel(Parcel source) {
            return new CartTotals(source);
        }

        @Override
        public CartTotals[] newArray(int size) {
            return new CartTotals[size];
        }
    };
}
