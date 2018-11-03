package com.dekalabs.magentorestapi.pojo.cart;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShippingMethod implements Parcelable {

    @JsonProperty("carrier_code")
    public String carrierCode;
    @JsonProperty("method_code")
    public String methodCode;
    @JsonProperty("carrier_title")
    public String carrierTitle;
    @JsonProperty("method_title")
    public String methodTitle;

    public double amount;
    @JsonProperty("base_amount")
    public double baseAmount;

    public Boolean available;
    @JsonProperty("error_message")
    public String errorMessage;

    @JsonProperty("price_excl_tax")
    public double priceExclTax;

    @JsonProperty("price_incl_tax")
    public double priceInclTax;

    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public String getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(String methodCode) {
        this.methodCode = methodCode;
    }

    public String getCarrierTitle() {
        return carrierTitle;
    }

    public void setCarrierTitle(String carrierTitle) {
        this.carrierTitle = carrierTitle;
    }

    public String getMethodTitle() {
        return methodTitle;
    }

    public void setMethodTitle(String methodTitle) {
        this.methodTitle = methodTitle;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(double baseAmount) {
        this.baseAmount = baseAmount;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public double getPriceExclTax() {
        return priceExclTax;
    }

    public void setPriceExclTax(double priceExclTax) {
        this.priceExclTax = priceExclTax;
    }

    public double getPriceInclTax() {
        return priceInclTax;
    }

    public void setPriceInclTax(double priceInclTax) {
        this.priceInclTax = priceInclTax;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.carrierCode);
        dest.writeString(this.methodCode);
        dest.writeString(this.carrierTitle);
        dest.writeString(this.methodTitle);
        dest.writeDouble(this.amount);
        dest.writeDouble(this.baseAmount);
        dest.writeValue(this.available);
        dest.writeString(this.errorMessage);
        dest.writeDouble(this.priceExclTax);
        dest.writeDouble(this.priceInclTax);
    }

    public ShippingMethod() {
    }

    protected ShippingMethod(Parcel in) {
        this.carrierCode = in.readString();
        this.methodCode = in.readString();
        this.carrierTitle = in.readString();
        this.methodTitle = in.readString();
        this.amount = in.readDouble();
        this.baseAmount = in.readDouble();
        this.available = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.errorMessage = in.readString();
        this.priceExclTax = in.readDouble();
        this.priceInclTax = in.readDouble();
    }

    public static final Parcelable.Creator<ShippingMethod> CREATOR = new Parcelable.Creator<ShippingMethod>() {
        @Override
        public ShippingMethod createFromParcel(Parcel source) {
            return new ShippingMethod(source);
        }

        @Override
        public ShippingMethod[] newArray(int size) {
            return new ShippingMethod[size];
        }
    };
}