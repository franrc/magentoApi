package com.dekalabs.magentorestapi.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Address extends RealmObject implements Parcelable {

    @PrimaryKey
    @JsonIgnore
    private Long id;

    @JsonProperty("customer_id")
    private Long customerId;

    private String company;

    private String regionCode;
    @JsonIgnore
    private String region;
    private Long regionId;
    private String countryId;

    private String email;

    @JsonIgnore
    private String streetName;

    @JsonIgnore
    private String addressName;

    private String telephone;
    private String fax;
    private String postcode;
    private String city;
    private String firstname;
    private String lastname;

    private String secondaryPhone;

    private String middlename;
    private String prefix;
    private String suffix;

    private int sameAsBilling;

    @JsonProperty("vat_id")
    private String vatId;

    @JsonProperty("default_billing")
    private boolean defaultBilling;

    @JsonProperty("default_shipping")
    private boolean defaultShipping;

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

    @JsonGetter
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


    //    @SuppressWarnings("unchecked")
//    @JsonProperty("region")
//    private void unpackRegion(Map<String,Object> region) {
//
//        if(region != null) {
//            Object object = region.get("region_code");
//
//            if(object != null) {
//                this.regionCode = object.toString();
//            }
//
//            Object objRegion = region.get("region");
//
//            if(objRegion != null) {
//                this.region = objRegion.toString();
//            }
//
//            Object objRegionId = region.get("region_id");
//
//            if(objRegionId != null) {
//                this.regionId = (Long)objRegionId;
//            }
//        }
//    }

    @SuppressWarnings("unchecked")
    @JsonSetter("region")
    private void unpackRegion(Map<String,Object> attrs) {

        if(attrs != null) {
            Object codeObj = attrs.get("region_code");

            if(codeObj != null) {
                this.regionCode = codeObj.toString();
            }

            Object regionObj = attrs.get("region");

            if(regionObj != null) {
                this.region = regionObj.toString();
            }

            Object idObj = attrs.get("region_id");

            if(idObj != null) {
                this.regionId = Long.valueOf(idObj.toString());
            }
        }
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("customAttributes")
    private void unpackCustomAttributes(Map<String,Object> attrs) {

        if(attrs != null) {
            Object object = attrs.get("wjh_phone_secondary");

            if(object != null) {
                this.secondaryPhone = object.toString();
            }
        }
    }

    @JsonProperty("customAttributes")
    private Map<String, Object> setCustomAttributes() {
        Map<String,Object> attrs = new HashMap<>();
        attrs.put("wjh_phone_secondary", this.secondaryPhone);

        return attrs;
    }


    @JsonProperty("street")
    public List<String> getStreet() {
        List<String> street = new ArrayList<>();
        street.add(this.streetName);
        street.add(this.addressName);

        return street;
    }

    @JsonProperty("street")
    public void setStreet(List<String> street) {
        this.streetName = street.get(0);
        this.addressName = street.size() > 1 ? street.get(1) : "";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSameAsBilling() {
        return sameAsBilling;
    }

    public void setSameAsBilling(int sameAsBilling) {
        this.sameAsBilling = sameAsBilling;
    }

    public boolean isDefaultBilling() {
        return defaultBilling;
    }

    public void setDefaultBilling(boolean defaultBilling) {
        this.defaultBilling = defaultBilling;
    }

    public boolean isDefaultShipping() {
        return defaultShipping;
    }

    public void setDefaultShipping(boolean defaultShipping) {
        this.defaultShipping = defaultShipping;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.customerId);
        dest.writeString(this.company);
        dest.writeString(this.regionCode);
        dest.writeString(this.region);
        dest.writeValue(this.regionId);
        dest.writeString(this.countryId);
        dest.writeString(this.streetName);
        dest.writeString(this.addressName);
        dest.writeString(this.telephone);
        dest.writeString(this.fax);
        dest.writeString(this.postcode);
        dest.writeString(this.city);
        dest.writeString(this.firstname);
        dest.writeString(this.lastname);
        dest.writeString(this.secondaryPhone);
        dest.writeString(this.middlename);
        dest.writeString(this.prefix);
        dest.writeString(this.suffix);
        dest.writeString(this.vatId);
        dest.writeString(this.email);
        dest.writeInt(this.sameAsBilling);
        dest.writeInt(this.defaultBilling ? 1 : 0);
        dest.writeInt(this.defaultShipping ? 1 : 0);
    }

    public Address() {
    }

    protected Address(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.customerId = (Long) in.readValue(Long.class.getClassLoader());
        this.company = in.readString();
        this.regionCode = in.readString();
        this.region = in.readString();
        this.regionId = (Long) in.readValue(Long.class.getClassLoader());
        this.countryId = in.readString();
        this.streetName = in.readString();
        this.addressName = in.readString();
        this.telephone = in.readString();
        this.fax = in.readString();
        this.postcode = in.readString();
        this.city = in.readString();
        this.firstname = in.readString();
        this.lastname = in.readString();
        this.secondaryPhone = in.readString();
        this.middlename = in.readString();
        this.prefix = in.readString();
        this.suffix = in.readString();
        this.vatId = in.readString();
        this.email = in.readString();
        this.sameAsBilling = in.readInt();
        this.defaultBilling = in.readInt() == 1;
        this.defaultShipping = in.readInt() == 1;
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel source) {
            return new Address(source);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getSecondaryPhone() {
        return secondaryPhone;
    }

    public void setSecondaryPhone(String secondaryPhone) {
        this.secondaryPhone = secondaryPhone;
    }

    public String getPostalCodeCity() {
        String postalCodeCity = postcode != null ? postcode : "";

        if(postalCodeCity.length() > 0)
            postalCodeCity += " ";

        postalCodeCity += region + " " + city;

        return postalCodeCity;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        return id != null ? id.equals(address.id) : address.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
