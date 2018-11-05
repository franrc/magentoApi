package com.dekalabs.magentorestapi.utils;

public class Pair<T, D> {

    private T first;
    private D second;


    public Pair() {
    }

    public Pair(T first, D second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public D getSecond() {
        return second;
    }

    public void setSecond(D second) {
        this.second = second;
    }
}
