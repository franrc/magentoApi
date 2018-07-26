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
    @JsonSerialize(as = CustomAttribute.class)
    private CustomAttribute attribute;

    private String name;

    private RealmList<String> value;

    public void generatePrimaryKey(Long idProduct) {
        id = String.valueOf(idProduct) + "/" + attribute.getAttributeCode();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CustomAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(CustomAttribute attribute) {
        this.attribute = attribute;
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
        dest.writeParcelable(this.attribute, flags);
        dest.writeStringList(this.value);
        dest.writeString(name);
    }

    public ProductAttributes() {
    }

    protected ProductAttributes(Parcel in) {
        this.id = in.readString();
        this.attribute = in.readParcelable(CustomAttribute.class.getClassLoader());
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
}
