package com.dekalabs.magentorestapi.dto;

import com.dekalabs.magentorestapi.pojo.Address;
import com.dekalabs.magentorestapi.pojo.cart.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PlaceOrderDTO {

    private Address billingAddress;

    private PaymentAssignementMethodDTO paymentMethod;

    private String email;

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public PaymentAssignementMethodDTO getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentAssignementMethodDTO paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
