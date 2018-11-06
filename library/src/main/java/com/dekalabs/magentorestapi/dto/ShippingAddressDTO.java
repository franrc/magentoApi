package com.dekalabs.magentorestapi.dto;

import com.dekalabs.magentorestapi.pojo.Address;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ShippingAddressDTO {

    private AddressInformation addressInformation;

    public ShippingAddressDTO() {
    }

    public ShippingAddressDTO(AddressInformation addressInformation) {
        this.addressInformation = addressInformation;
    }

    public AddressInformation getAddressInformation() {
        return addressInformation;
    }

    public void setAddressInformation(AddressInformation addressInformation) {
        this.addressInformation = addressInformation;
    }

    public static class AddressInformation {

        @JsonProperty("shipping_address")
        private Address shippingAddress;

        @JsonProperty("billing_address")
        private Address billingAddress;

        @JsonProperty("shipping_method_code")
        private String shippingMethodCode;

        @JsonProperty("shipping_carrier_code")
        private String shippingCarrierCode;

        public Address getShippingAddress() {
            return shippingAddress;
        }

        public void setShippingAddress(Address shippingAddress) {
            this.shippingAddress = shippingAddress;
        }

        public String getShippingMethodCode() {
            return shippingMethodCode;
        }

        public void setShippingMethodCode(String shippingMethodCode) {
            this.shippingMethodCode = shippingMethodCode;
        }

        public String getShippingCarrierCode() {
            return shippingCarrierCode;
        }

        public void setShippingCarrierCode(String shippingCarrierCode) {
            this.shippingCarrierCode = shippingCarrierCode;
        }

        public Address getBillingAddress() {
            return billingAddress;
        }

        public void setBillingAddress(Address billingAddress) {
            this.billingAddress = billingAddress;
        }
    }
}
