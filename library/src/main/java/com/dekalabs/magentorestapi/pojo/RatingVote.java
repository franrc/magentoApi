package com.dekalabs.magentorestapi.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RatingVote {

    @JsonProperty("vote_id")
    private Long id;

    private double percent;
    private int value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
