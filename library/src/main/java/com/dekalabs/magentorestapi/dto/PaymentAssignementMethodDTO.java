package com.dekalabs.magentorestapi.dto;

public class PaymentAssignementMethodDTO {

    private String method;

    public PaymentAssignementMethodDTO() {
    }

    public PaymentAssignementMethodDTO(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
