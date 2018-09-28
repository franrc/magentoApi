package com.dekalabs.magentorestapi.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class Filter implements Parcelable {

    @JsonProperty("filter_code")
    private String filterCode;

    @JsonProperty("filter_name")
    private String name;

    @JsonProperty("values")
    private List<FilterValues> filterValues;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilterCode() {
        return filterCode;
    }

    public void setFilterCode(String filterCode) {
        this.filterCode = filterCode;

        //TODO HACK!
        if(this.filterCode.equals("category_ids"))
            this.filterCode = "category_id";
    }

    public List<FilterValues> getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(List<FilterValues> filterValues) {
        this.filterValues = filterValues;
    }

    public String getCommaSeparatedValues() {
        if(filterValues == null || filterValues.size() == 0) return null;

        StringBuilder builder = new StringBuilder();

        for(FilterValues fv : filterValues) {
            builder.append(fv.getValue()).append(",");
        }

        return builder.toString().substring(0, builder.length() -1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filterCode);
        dest.writeString(this.name);
        dest.writeList(this.filterValues);
    }

    public Filter() {
    }

    protected Filter(Parcel in) {
        this.filterCode = in.readString();
        this.name = in.readString();
        this.filterValues = new ArrayList<FilterValues>();
        in.readList(this.filterValues, FilterValues.class.getClassLoader());
    }

    public static final Creator<Filter> CREATOR = new Creator<Filter>() {
        @Override
        public Filter createFromParcel(Parcel source) {
            return new Filter(source);
        }

        @Override
        public Filter[] newArray(int size) {
            return new Filter[size];
        }
    };



    public static class FilterValues implements Parcelable {

        private String label;
        private String value;
        private boolean selected;

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

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.label);
            dest.writeString(this.value);
            dest.writeByte(selected ? (byte)1 : (byte)0);
        }

        public FilterValues() {
        }

        protected FilterValues(Parcel in) {
            this.label = in.readString();
            this.value = in.readString();
            this.selected = in.readByte() == (byte)1;
        }

        public static final Creator<FilterValues> CREATOR = new Creator<FilterValues>() {
            @Override
            public FilterValues createFromParcel(Parcel source) {
                return new FilterValues(source);
            }

            @Override
            public FilterValues[] newArray(int size) {
                return new FilterValues[size];
            }
        };

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FilterValues that = (FilterValues) o;

            return value != null ? value.equals(that.value) : that.value == null;
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Filter filter = (Filter) o;

        return filterCode != null ? filterCode.equals(filter.filterCode) : filter.filterCode == null;
    }

    @Override
    public int hashCode() {
        return filterCode != null ? filterCode.hashCode() : 0;
    }
}
