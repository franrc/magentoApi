package com.dekalabs.magentorestapi.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class TierPrice {

    @JsonProperty("customer_group_id")
    private Long customerGroupId;

    private int qty;

    private double value;

    @JsonIgnore
    private double percentageValue;


    @SuppressWarnings("unchecked")
    @JsonProperty("extension_attributes")
    private void unpackExtended(Map<String,Object> extend) {

        if(extend != null) {
            Object objData = extend.get("base64_encoded_data");

            if(objData != null) {
                this.percentageValue = Double.parseDouble(objData.toString());
            }
        }
    }
}
