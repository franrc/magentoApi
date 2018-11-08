package com.dekalabs.magentorestapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentAssignementMethodDTO {

    private String method;

    @JsonProperty("additional_data")
    private CreditCardAdditionalData additionalData;

    public PaymentAssignementMethodDTO() {
    }

    public PaymentAssignementMethodDTO(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public CreditCardAdditionalData getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(CreditCardAdditionalData additionalData) {
        this.additionalData = additionalData;
    }

    public static class CreditCardAdditionalData {

         @JsonProperty("cc_cid")
         private String ccCid;

         @JsonProperty("cc_type")
         private String ccType;

         @JsonProperty("cc_exp_year")
         private String ccExpYear;

         @JsonProperty("cc_exp_month")
         private String ccExpMonth;

         @JsonProperty("cc_number")
         private String ccNumber;

         @JsonProperty("card_token_id")
         private String cardTokenId;

        public String getCcCid() {
            return ccCid;
        }

        public void setCcCid(String ccCid) {
            this.ccCid = ccCid;
        }

        public String getCcType() {
            return ccType;
        }

        public void setCcType(String ccType) {
            this.ccType = ccType;
        }

        public String getCcExpYear() {
            return ccExpYear;
        }

        public void setCcExpYear(String ccExpYear) {
            this.ccExpYear = ccExpYear;
        }

        public String getCcExpMonth() {
            return ccExpMonth;
        }

        public void setCcExpMonth(String ccExpMonth) {
            this.ccExpMonth = ccExpMonth;
        }

        public String getCcNumber() {
            return ccNumber;
        }

        public void setCcNumber(String ccNumber) {
            this.ccNumber = ccNumber;
        }

        public String getCardTokenId() {
            return cardTokenId;
        }

        public void setCardTokenId(String cardTokenId) {
            this.cardTokenId = cardTokenId;
        }
    }
}
