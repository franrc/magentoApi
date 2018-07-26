package com.dekalabs.magentorestapi.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

import io.realm.Realm;
import io.realm.RealmObject;

public class TierPrice extends RealmObject implements Parcelable {

    @JsonProperty("customer_group_id")
    private Long customerGroupId;

    private int qty;

    private double value;

    @JsonIgnore
    private double percentageValue;

    public Long getCustomerGroupId() {
        return customerGroupId;
    }

    public void setCustomerGroupId(Long customerGroupId) {
        this.customerGroupId = customerGroupId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getPercentageValue() {
        return percentageValue;
    }

    public void setPercentageValue(double percentageValue) {
        this.percentageValue = percentageValue;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("extension_attributes")
    private void unpackExtended(Map<String,Object> extend) {

        if(extend != null) {
            Object objData = extend.get("base64_encoded_data");

            if(objData != null) {
                this.percentageValue = Double.parseDouble(objData.toString());
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.customerGroupId);
        dest.writeInt(this.qty);
        dest.writeDouble(this.value);
        dest.writeDouble(this.percentageValue);
    }

    public TierPrice() {
    }

    protected TierPrice(Parcel in) {
        this.customerGroupId = (Long) in.readValue(Long.class.getClassLoader());
        this.qty = in.readInt();
        this.value = in.readDouble();
        this.percentageValue = in.readDouble();
    }

    public static final Parcelable.Creator<TierPrice> CREATOR = new Parcelable.Creator<TierPrice>() {
        @Override
        public TierPrice createFromParcel(Parcel source) {
            return new TierPrice(source);
        }

        @Override
        public TierPrice[] newArray(int size) {
            return new TierPrice[size];
        }
    };
}
