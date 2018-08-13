package com.dekalabs.magentorestapi.pojo.review;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ReviewItem {

    @JsonProperty("avg_rating_percent")
    private double avgRatingPercent;

    private int count;

    private List<Review> reviews;

    public double getAvgRatingPercent() {
        return avgRatingPercent;
    }

    public void setAvgRatingPercent(double avgRatingPercent) {
        this.avgRatingPercent = avgRatingPercent;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
