package com.dekalabs.magentorestapi.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Address extends RealmObject implements Parcelable {

    private Long id;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("region_code")
    private String regionCode;

    private String region;

    @JsonProperty("region_id")
    private Long regionId;

    @JsonProperty("country_id")
    private String countryId;
    private RealmList<String> street;
    private String company;
    private String telephone;
    private String fax;
    private String postcode;
    private String city;
    private String firstname;
    private String lastname;
    private String middlename;
    private String prefix;
    private String suffix;

    @JsonProperty("vat_id")
    private String vatId;

    @JsonProperty("defaultShipping")
    private boolean defaultShipping;

    @JsonProperty("default_billing")
    private boolean defaultBilling;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public RealmList<String> getStreet() {
        return street;
    }

    public void setStreet(RealmList<String> street) {
        this.street = street;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getVatId() {
        return vatId;
    }

    public void setVatId(String vatId) {
        this.vatId = vatId;
    }

    public boolean isDefaultShipping() {
        return defaultShipping;
    }

    public void setDefaultShipping(boolean defaultShipping) {
        this.defaultShipping = defaultShipping;
    }

    public boolean isDefaultBilling() {
        return defaultBilling;
    }

    public void setDefaultBilling(boolean defaultBilling) {
        this.defaultBilling = defaultBilling;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("region")
    private void unpackRegion(Map<String,Object> region) {

        if(region != null) {
            Object object = region.get("region_code");

            if(object != null) {
                this.regionCode = object.toString();
            }

            Object objRegion = region.get("region");

            if(objRegion != null) {
                this.region = objRegion.toString();
            }

            Object objRegionId = region.get("region_id");

            if(objRegionId != null) {
                this.regionId = (Long)objRegionId;
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
        dest.writeValue(this.customerId);
        dest.writeString(this.regionCode);
        dest.writeString(this.region);
        dest.writeValue(this.regionId);
        dest.writeString(this.countryId);
        dest.writeStringList(this.street);
        dest.writeString(this.company);
        dest.writeString(this.telephone);
        dest.writeString(this.fax);
        dest.writeString(this.postcode);
        dest.writeString(this.city);
        dest.writeString(this.firstname);
        dest.writeString(this.lastname);
        dest.writeString(this.middlename);
        dest.writeString(this.prefix);
        dest.writeString(this.suffix);
        dest.writeString(this.vatId);
        dest.writeByte(this.defaultShipping ? (byte) 1 : (byte) 0);
        dest.writeByte(this.defaultBilling ? (byte) 1 : (byte) 0);
    }

    public Address() {
    }

    protected Address(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.customerId = (Long) in.readValue(Long.class.getClassLoader());
        this.regionCode = in.readString();
        this.region = in.readString();
        this.regionId = (Long) in.readValue(Long.class.getClassLoader());
        this.countryId = in.readString();
        this.street = new RealmList<>();
        this.street.addAll(in.createStringArrayList());
        this.company = in.readString();
        this.telephone = in.readString();
        this.fax = in.readString();
        this.postcode = in.readString();
        this.city = in.readString();
        this.firstname = in.readString();
        this.lastname = in.readString();
        this.middlename = in.readString();
        this.prefix = in.readString();
        this.suffix = in.readString();
        this.vatId = in.readString();
        this.defaultShipping = in.readByte() != 0;
        this.defaultBilling = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Address> CREATOR = new Parcelable.Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel source) {
            return new Address(source);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };
}
