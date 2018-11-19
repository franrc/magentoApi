package com.dekalabs.magentorestapi.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.HashMap;
import java.util.Map;

public class CustomerRegisterDTO {

    private Customer customer;
    private String password;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public static final class Customer {

        private static final Long DEFAULT_WEBSITE_ID = 2L;
        private static final Long DEFAULT_STORE_ID = 1L;

        @JsonProperty("dob")
        private String birthdate;

        private String email;
        private String firstname;
        private String lastname;

        private String telephone;

        @JsonProperty("store_id")
        private Long storeId = DEFAULT_STORE_ID;

        @JsonProperty("website_id")
        private Long websiteId = DEFAULT_WEBSITE_ID;

        @JsonIgnore
        private boolean isSubscribed;

        public String getBirthdate() {
            return birthdate;
        }

        public void setBirthdate(String birthdate) {
            this.birthdate = birthdate;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
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

        public Long getWebsiteId() {
            return websiteId;
        }

        public void setWebsiteId(Long websiteId) {
            this.websiteId = websiteId;
        }

        public boolean isSubscribed() {
            return isSubscribed;
        }

        public void setSubscribed(boolean subscribed) {
            isSubscribed = subscribed;
        }

        public Long getStoreId() {
            return storeId;
        }

        public void setStoreId(Long storeId) {
            this.storeId = storeId;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        @JsonGetter("extension_attributes")
        private Map<String, Object> setExtensionAttibutes() {
            Map<String, Object> map = new HashMap<>();

            map.put("is_subscribed", String.valueOf(isSubscribed));

            return map;
        }
    }
}
