package com.dekalabs.magentorestapi.dto;

import java.util.ArrayList;
import java.util.List;

public class ReviewPost {

    private Long productId;
    private String nickname;
    private String title;
    private String detail;

    private List<RatingData> ratingData;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public List<RatingData> getRatingData() {
        return ratingData;
    }

    public void setRatingData(List<RatingData> ratingData) {
        this.ratingData = ratingData;
    }

    public void setRatingData(RatingData ratingData) {
        if(this.ratingData == null)
            this.ratingData = new ArrayList<>();

        this.ratingData.add(ratingData);
    }

    /**************************/

    public static class RatingData {

        private Long ratingId = 4L;
        private String ratingCode = "Rating";
        private int ratingValue;

        public RatingData() {
        }

        public RatingData(int ratingValue) {
            this.ratingValue = ratingValue;
        }

        public Long getRatingId() {
            return ratingId;
        }

        public void setRatingId(Long ratingId) {
            this.ratingId = ratingId;
        }

        public String getRatingCode() {
            return ratingCode;
        }

        public void setRatingCode(String ratingCode) {
            this.ratingCode = ratingCode;
        }

        public int getRatingValue() {
            return ratingValue;
        }

        public void setRatingValue(int ratingValue) {
            this.ratingValue = ratingValue;
        }
    }
}
