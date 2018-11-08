package com.dekalabs.magentorestapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CountryRegion {

    private String id;

    @JsonProperty("full_name_locale")
    private String fullNameLocale;

    @JsonProperty("available_regions")
    List<Region> regions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullNameLocale() {
        return fullNameLocale;
    }

    public void setFullNameLocale(String fullNameLocale) {
        this.fullNameLocale = fullNameLocale;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void setRegions(List<Region> regions) {
        this.regions = regions;
    }

    public static class Region {
        private Long id;
        private String code;
        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
