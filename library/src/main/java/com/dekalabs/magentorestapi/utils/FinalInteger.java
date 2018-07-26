package com.dekalabs.magentorestapi.utils;

public class FinalInteger {

    private int value;

    public FinalInteger(int value) {
        this.value = value;
    }

    public void addValue() {
        this.value++;
    }

    public void removeValue() {
        this.value--;
    }

    public int getValue() {
        return value;
    }
}
