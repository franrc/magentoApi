package com.dekalabs.magentorestapi.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class Address {

    private Long id;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("region_code")
    private String regionCode;

    private String region;

    @JsonProperty("region_id")
    private Long regionId;

    @JsonProperty("country_id")
    private String countryId;
    private List<String> street;
    private String company;
    private String telephone;
    private String fax;
    private String postcode;
    private String city;
    private String firstname;
    private String lastname;
    private String middlename;
    private String prefix;
    private String suffix;

    @JsonProperty("vat_id")
    private String vatId;

    @JsonProperty("defaultShipping")
    private boolean defaultShipping;

    @JsonProperty("default_billing")
    private boolean defaultBilling;




    @SuppressWarnings("unchecked")
    @JsonProperty("region")
    private void unpackRegion(Map<String,Object> region) {

        if(region != null) {
            Object object = region.get("region_code");

            if(object != null) {
                this.regionCode = object.toString();
            }

            Object objRegion = region.get("region");

            if(objRegion != null) {
                this.region = objRegion.toString();
            }

            Object objRegionId = region.get("region_id");

            if(objRegionId != null) {
                this.regionId = (Long)objRegionId;
            }
        }
    }
}
