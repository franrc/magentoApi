package com.dekalabs.magentorestapi.utils;

public class FinalLong {

    private Long value;

    public FinalLong(Long value) {
        this.value = value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public void addValue() {
        this.value++;
    }

    public void removeValue() {
        this.value--;
    }

    public Long getValue() {
        return value;
    }
}
