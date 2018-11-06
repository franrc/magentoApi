package com.dekalabs.magentorestapi.dto;

public class CustomerEmailCheckerDTO {

    private String customerEmail;

    public CustomerEmailCheckerDTO(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public CustomerEmailCheckerDTO() {
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}
