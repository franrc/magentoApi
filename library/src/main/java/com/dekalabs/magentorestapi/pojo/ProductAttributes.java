package com.dekalabs.magentorestapi.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class ProductAttributes extends RealmObject implements Parcelable {

    @PrimaryKey
    private String id;

    @JsonProperty("attribute_code")
//    @JsonSerialize(as = CustomAttribute.class)
//    private CustomAttribute attribute;
    private String attributeCode;

    private String name;

    private RealmList<String> value;

    public void generatePrimaryKey(Long idProduct) {
        id = String.valueOf(idProduct) + "/" + attributeCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttributeCode() {
        return attributeCode;
    }

    public void setAttribute(String attribute) {
        this.attributeCode = attribute;
    }

    public String getValue() {
        return value != null && value.size() > 0 ? value.get(0) : null;
    }

    public List<String> getValues() {
        return value;
    }

    public void setValue(RealmList<String> value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.attributeCode);
        dest.writeStringList(this.value);
        dest.writeString(name);
    }

    public ProductAttributes() {
    }

    public ProductAttributes(String code) {
        this.attributeCode = code;
    }

    protected ProductAttributes(Parcel in) {
        this.id = in.readString();
        this.attributeCode = in.readString();
        this.value = new RealmList<>();
        this.value.addAll(in.createStringArrayList());
        this.name = in.readString();
    }

    public static final Parcelable.Creator<ProductAttributes> CREATOR = new Parcelable.Creator<ProductAttributes>() {
        @Override
        public ProductAttributes createFromParcel(Parcel source) {
            return new ProductAttributes(source);
        }

        @Override
        public ProductAttributes[] newArray(int size) {
            return new ProductAttributes[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductAttributes that = (ProductAttributes) o;

        return attributeCode != null ? attributeCode.equals(that.attributeCode) : that.attributeCode == null;
    }

    @Override
    public int hashCode() {
        return attributeCode != null ? attributeCode.hashCode() : 0;
    }
}
