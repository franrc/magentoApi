package com.dekalabs.magentorestapi.dto;

import com.dekalabs.magentorestapi.pojo.Customer;

public class UpdateCustomerDTO {

    Customer customer;

    public UpdateCustomerDTO(Customer customer) {
        this.customer = customer;
    }

    public UpdateCustomerDTO() {
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
