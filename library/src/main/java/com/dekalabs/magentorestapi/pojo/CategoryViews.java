package com.dekalabs.magentorestapi.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class CategoryViews implements Parcelable {

    private Long id;
    private String name;

    private Navigation navigation;

    public Navigation getNavigation() {
        return navigation;
    }

    public void setNavigation(Navigation navigation) {
        this.navigation = navigation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Product> getProductList() {
        return navigation != null ? navigation.products : null;
    }


    @SuppressWarnings("unchecked")
    @JsonProperty("category")
    private void unpackCategoryData(Map<String,Object> category) {

        if(category != null) {
            Object objId = category.get("id");
            Object objName = category.get("name");

            if(objId != null) {
                this.id = Long.valueOf(objId.toString());
            }

            if(objName != null) {
                this.name = objName.toString();
            }
        }
    }


    public static class Navigation implements Parcelable {
        private List<Product> products;

        public List<Product> getProducts() {
            return products;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(this.products);
        }

        public Navigation() {
        }

        protected Navigation(Parcel in) {
            this.products = in.createTypedArrayList(Product.CREATOR);
        }

        public static final Parcelable.Creator<Navigation> CREATOR = new Parcelable.Creator<Navigation>() {
            @Override
            public Navigation createFromParcel(Parcel source) {
                return new Navigation(source);
            }

            @Override
            public Navigation[] newArray(int size) {
                return new Navigation[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeParcelable(this.navigation, flags);
    }

    public CategoryViews() {
    }

    protected CategoryViews(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.navigation = in.readParcelable(Navigation.class.getClassLoader());
    }

    public static final Parcelable.Creator<CategoryViews> CREATOR = new Parcelable.Creator<CategoryViews>() {
        @Override
        public CategoryViews createFromParcel(Parcel source) {
            return new CategoryViews(source);
        }

        @Override
        public CategoryViews[] newArray(int size) {
            return new CategoryViews[size];
        }
    };
}
