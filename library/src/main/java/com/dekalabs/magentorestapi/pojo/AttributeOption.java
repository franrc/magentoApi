package com.dekalabs.magentorestapi.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class AttributeOption extends RealmObject implements Parcelable {

    @PrimaryKey
    private Long id;

    private String label;
    private String value;

    @Ignore
    private List<String> products;

    @Index
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

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
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
        dest.writeStringList(this.products);
    }

    public AttributeOption() {
    }

    public AttributeOption(String text) {

    }

    protected AttributeOption(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.label = in.readString();
        this.value = in.readString();
        this.attributeCode = in.readString();

        List<String> products = in.createStringArrayList();

        if(products != null) {
            this.products = new ArrayList<>();
            this.products.addAll(products);
        }
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

        AttributeOption option = (AttributeOption) o;

        if (id != null ? !id.equals(option.id) : option.id != null) return false;
        return attributeCode != null ? attributeCode.equals(option.attributeCode) : option.attributeCode == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (attributeCode != null ? attributeCode.hashCode() : 0);
        return result;
    }
}
