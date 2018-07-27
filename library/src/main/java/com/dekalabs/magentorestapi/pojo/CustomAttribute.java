package com.dekalabs.magentorestapi.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CustomAttribute extends RealmObject implements Parcelable {

    @PrimaryKey
    @JsonProperty("attribute_code")
    private String attributeCode;

    @JsonProperty("attribute_id")
    private Long attributeId;

    private RealmList<AttributeOption> options;

    public CustomAttribute(String attributeCode) {
        this.attributeCode = attributeCode;
    }

    public String getAttributeCode() {
        return attributeCode;
    }

    public void setAttributeCode(String attributeCode) {
        this.attributeCode = attributeCode;
    }


    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public RealmList<AttributeOption> getOptions() {
        return options;
    }

    public void setOptions(RealmList<AttributeOption> options) {
        this.options = options;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.attributeCode);
        dest.writeValue(this.attributeId);
        dest.writeTypedList(this.options);
    }

    public CustomAttribute() {
    }

    protected CustomAttribute(Parcel in) {
        this.attributeCode = in.readString();
        this.attributeId = (Long) in.readValue(Long.class.getClassLoader());
        this.options = new RealmList<>();
        this.options.addAll(in.createTypedArrayList(AttributeOption.CREATOR));
    }

    public static final Creator<CustomAttribute> CREATOR = new Creator<CustomAttribute>() {
        @Override
        public CustomAttribute createFromParcel(Parcel source) {
            return new CustomAttribute(source);
        }

        @Override
        public CustomAttribute[] newArray(int size) {
            return new CustomAttribute[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomAttribute that = (CustomAttribute) o;

        return attributeCode != null ? attributeCode.equals(that.attributeCode) : that.attributeCode == null;
    }

    @Override
    public int hashCode() {
        return attributeCode != null ? attributeCode.hashCode() : 0;
    }
}
