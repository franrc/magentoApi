
package com.dekalabs.magentorestapi.pojo.cart;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.realm.RealmObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Currency extends RealmObject implements Parcelable {

    @JsonProperty("global_currency_code")
    private String globalCurrencyCode;

    @JsonProperty("base_currency_code")
    private String baseCurrencyCode;

    @JsonProperty("store_currency_code")
    private String storeCurrencyCode;

    @JsonProperty("quote_currency_code")
    private String quoteCurrencyCode;

    @JsonProperty("store_to_base_rate")
    private Integer storeToBaseRate;

    @JsonProperty("store_to_quote_rate")
    private Integer storeToQuoteRate;

    @JsonProperty("base_to_global_rate")
    private Double baseToGlobalRate;

    @JsonProperty("base_to_quote_rate")
    private Double baseToQuoteRate;

    public String getGlobalCurrencyCode() {
        return globalCurrencyCode;
    }

    public void setGlobalCurrencyCode(String globalCurrencyCode) {
        this.globalCurrencyCode = globalCurrencyCode;
    }

    public String getBaseCurrencyCode() {
        return baseCurrencyCode;
    }

    public void setBaseCurrencyCode(String baseCurrencyCode) {
        this.baseCurrencyCode = baseCurrencyCode;
    }

    public String getStoreCurrencyCode() {
        return storeCurrencyCode;
    }

    public void setStoreCurrencyCode(String storeCurrencyCode) {
        this.storeCurrencyCode = storeCurrencyCode;
    }

    public String getQuoteCurrencyCode() {
        return quoteCurrencyCode;
    }

    public void setQuoteCurrencyCode(String quoteCurrencyCode) {
        this.quoteCurrencyCode = quoteCurrencyCode;
    }

    public Integer getStoreToBaseRate() {
        return storeToBaseRate;
    }

    public void setStoreToBaseRate(Integer storeToBaseRate) {
        this.storeToBaseRate = storeToBaseRate;
    }

    public Integer getStoreToQuoteRate() {
        return storeToQuoteRate;
    }

    public void setStoreToQuoteRate(Integer storeToQuoteRate) {
        this.storeToQuoteRate = storeToQuoteRate;
    }

    public Double getBaseToGlobalRate() {
        return baseToGlobalRate;
    }

    public void setBaseToGlobalRate(Double baseToGlobalRate) {
        this.baseToGlobalRate = baseToGlobalRate;
    }

    public Double getBaseToQuoteRate() {
        return baseToQuoteRate;
    }

    public void setBaseToQuoteRate(Double baseToQuoteRate) {
        this.baseToQuoteRate = baseToQuoteRate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.globalCurrencyCode);
        dest.writeString(this.baseCurrencyCode);
        dest.writeString(this.storeCurrencyCode);
        dest.writeString(this.quoteCurrencyCode);
        dest.writeValue(this.storeToBaseRate);
        dest.writeValue(this.storeToQuoteRate);
        dest.writeValue(this.baseToGlobalRate);
        dest.writeValue(this.baseToQuoteRate);
    }

    public Currency() {
    }

    protected Currency(Parcel in) {
        this.globalCurrencyCode = in.readString();
        this.baseCurrencyCode = in.readString();
        this.storeCurrencyCode = in.readString();
        this.quoteCurrencyCode = in.readString();
        this.storeToBaseRate = (Integer) in.readValue(Integer.class.getClassLoader());
        this.storeToQuoteRate = (Integer) in.readValue(Integer.class.getClassLoader());
        this.baseToGlobalRate = (Double) in.readValue(Double.class.getClassLoader());
        this.baseToQuoteRate = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Parcelable.Creator<Currency> CREATOR = new Parcelable.Creator<Currency>() {
        @Override
        public Currency createFromParcel(Parcel source) {
            return new Currency(source);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };
}
