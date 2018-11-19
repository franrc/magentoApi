package com.dekalabs.magentorestapi.dto;

public class CustomerLoginForSessionDTO {

    private String email;
    private String password;

    public CustomerLoginForSessionDTO() {
    }

    public CustomerLoginForSessionDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
