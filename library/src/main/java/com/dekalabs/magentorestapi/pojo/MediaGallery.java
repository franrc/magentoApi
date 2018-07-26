package com.dekalabs.magentorestapi.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

import io.realm.RealmObject;

public class MediaGallery extends RealmObject implements Parcelable {

    private Long id;

    @JsonProperty("media_type")
    private String mediaType;

    private String label;

    private int position;
    private boolean disabled;
    private String file;

    private String contentBase64EncodedData;
    private String contentType;
    private String contentName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getContentBase64EncodedData() {
        return contentBase64EncodedData;
    }

    public void setContentBase64EncodedData(String contentBase64EncodedData) {
        this.contentBase64EncodedData = contentBase64EncodedData;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("content")
    private void unpackStock(Map<String,Object> content) {

        if(content != null) {
            Object objData = content.get("base64_encoded_data");

            if(objData != null) {
                this.contentBase64EncodedData = objData.toString();
            }

            Object objType = content.get("type");

            if(objType != null) {
                this.contentType = objType.toString();
            }

            Object objName = content.get("name");

            if(objName != null) {
                this.contentName = objName.toString();
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.mediaType);
        dest.writeString(this.label);
        dest.writeInt(this.position);
        dest.writeByte(this.disabled ? (byte) 1 : (byte) 0);
        dest.writeString(this.file);
        dest.writeString(this.contentBase64EncodedData);
        dest.writeString(this.contentType);
        dest.writeString(this.contentName);
    }

    public MediaGallery() {
    }

    protected MediaGallery(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.mediaType = in.readString();
        this.label = in.readString();
        this.position = in.readInt();
        this.disabled = in.readByte() != 0;
        this.file = in.readString();
        this.contentBase64EncodedData = in.readString();
        this.contentType = in.readString();
        this.contentName = in.readString();
    }

    public static final Parcelable.Creator<MediaGallery> CREATOR = new Parcelable.Creator<MediaGallery>() {
        @Override
        public MediaGallery createFromParcel(Parcel source) {
            return new MediaGallery(source);
        }

        @Override
        public MediaGallery[] newArray(int size) {
            return new MediaGallery[size];
        }
    };
}
