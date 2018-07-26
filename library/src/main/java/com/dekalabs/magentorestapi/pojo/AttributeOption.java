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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.label);
        dest.writeString(this.value);
    }

    public AttributeOption() {
    }

    protected AttributeOption(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.label = in.readString();
        this.value = in.readString();
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
}
