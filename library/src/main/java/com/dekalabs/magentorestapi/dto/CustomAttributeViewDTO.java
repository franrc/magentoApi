package com.dekalabs.magentorestapi.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.dekalabs.magentorestapi.pojo.AttributeOption;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CustomAttributeViewDTO {

    @JsonProperty("configurable_product_options")
    private List<CustomAttributeView> attributeViews;

    public List<CustomAttributeView> getAttributeViews() {
        return attributeViews;
    }

    public void setAttributeViews(List<CustomAttributeView> attributeViews) {
        this.attributeViews = attributeViews;
    }

    public static class CustomAttributeView implements Parcelable {

        @JsonProperty("code")
        private String code;
        private Long id;
        private String label;

        private List<AttributeOption> options;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public List<AttributeOption> getOptions() {
            return options;
        }

        public void setOptions(List<AttributeOption> options) {
            this.options = options;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.code);
            dest.writeValue(this.id);
            dest.writeTypedList(this.options);
            dest.writeString(this.label);
        }

        public CustomAttributeView() {
        }

        protected CustomAttributeView(Parcel in) {
            this.code = in.readString();
            this.id = (Long) in.readValue(Long.class.getClassLoader());
            this.options = in.createTypedArrayList(AttributeOption.CREATOR);
            this.label = in.readString();
        }

        public static final Creator<CustomAttributeView> CREATOR = new Creator<CustomAttributeView>() {
            @Override
            public CustomAttributeView createFromParcel(Parcel source) {
                return new CustomAttributeView(source);
            }

            @Override
            public CustomAttributeView[] newArray(int size) {
                return new CustomAttributeView[size];
            }
        };

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CustomAttributeView that = (CustomAttributeView) o;

            return code != null ? code.equals(that.code) : that.code == null;
        }

        @Override
        public int hashCode() {
            return code != null ? code.hashCode() : 0;
        }
    }
}
