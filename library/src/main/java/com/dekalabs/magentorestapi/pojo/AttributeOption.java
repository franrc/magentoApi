package com.dekalabs.magentorestapi.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AttributeOption extends RealmObject implements Parcelable {

    @PrimaryKey
    private Long id;

    private String label;
    private String value;

    private String attributeCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAttributeCode() {
        return attributeCode;
    }

    public void setAttributeCode(String attributeCode) {
        this.attributeCode = attributeCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.label);
        dest.writeString(this.value);
        dest.writeString(this.attributeCode);
    }

    public AttributeOption() {
    }

    protected AttributeOption(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.label = in.readString();
        this.value = in.readString();
        this.attributeCode = in.readString();
    }

    public static final Parcelable.Creator<AttributeOption> CREATOR = new Parcelable.Creator<AttributeOption>() {
        @Override
        public AttributeOption createFromParcel(Parcel source) {
            return new AttributeOption(source);
        }

        @Override
        public AttributeOption[] newArray(int size) {
            return new AttributeOption[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttributeOption that = (AttributeOption) o;

        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
