package com.dekalabs.magentorestapi.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.dekalabs.magentorestapi.Jackson;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Category extends RealmObject implements Parcelable {

    private Long id;

    @JsonProperty("parent_id")
    private Long parentId;

    private String name;

    @JsonProperty("is_active")
    private boolean isActive;

    private int position;

    private int level;

    private String children;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("created_at")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updated_at")
    private Date updatedAt;

    private String path;

    @JsonProperty("product_count")
    private int productCount;

    @JsonProperty("children_data")
    private RealmList<Category> childrenData;

    @JsonProperty("include_in_menu")
    private boolean includeInMenu;

    private String image;

    private boolean hasChildren;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getChildren() {
        return children;
    }

    public void setChildren(String children) {
        this.children = children;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public List<Category> getChildrenData() {
        return childrenData;
    }

    public void setChildrenData(RealmList<Category> childrenData) {
        this.childrenData = childrenData;
    }

    public boolean isIncludeInMenu() {
        return includeInMenu;
    }

    public void setIncludeInMenu(boolean includeInMenu) {
        this.includeInMenu = includeInMenu;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isHasChildren() {
        return (children != null && !children.isEmpty()) || (childrenData != null && childrenData.size() > 0);
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.parentId);
        dest.writeString(this.name);
        dest.writeByte(this.isActive ? (byte) 1 : (byte) 0);
        dest.writeInt(this.position);
        dest.writeInt(this.level);
        dest.writeString(this.children);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
        dest.writeString(this.path);
        dest.writeInt(this.productCount);
        dest.writeList(this.childrenData);
        dest.writeByte(this.includeInMenu ? (byte) 1 : (byte) 0);
        dest.writeString(this.image);
    }

    public Category() {
    }

    protected Category(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.parentId = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.isActive = in.readByte() != 0;
        this.position = in.readInt();
        this.level = in.readInt();
        this.children = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        this.path = in.readString();
        this.productCount = in.readInt();
        this.childrenData = new RealmList<>();
        in.readList(this.childrenData, Category.class.getClassLoader());
        this.includeInMenu = in.readByte() != 0;
        this.image = in.readString();
    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };


    @SuppressWarnings("unchecked")
    @JsonProperty("custom_attributes")
    private void unpackCustomAttrs(List<Map<String,Object>> attrs) {

        if(attrs != null) {

            for(Map<String, Object> attrMap : attrs) {

                Object attrCode = attrMap.get("attribute_code");

                if(attrCode != null && attrCode.toString().equals("image")) {
                    image = (String)attrMap.get("value");

                    return;
                }

            }
        }
    }
}
