package com.dekalabs.magentorestapi.dto;

import com.dekalabs.magentorestapi.pojo.Address;

public class AddressDTO {

    private Address address;

    public AddressDTO(Address address) {
        this.address = address;
    }

    public AddressDTO() {
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
