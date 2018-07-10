package com.dekalabs.magentorestapi.config;

public class MagentoRestConfiguration {

    private static String APP_URL;
    private static String MEDIA_URL_PATH;
    private static String ACCESS_TOKEN;
    private static String CONSUMER_KEY;
    private static String CONSUMER_SECRET;


    public static class Builder {

        private String appUrl;
        private String accessToken;
        private String consumerKey;
        private String consumerSecret;
        private String mediaUrl;

        public Builder setAppUrl(String appUrl) {
            this.appUrl = appUrl;
            return this;
        }

        public Builder setAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder setConsumerKey(String consumerKey) {
            this.consumerKey = consumerKey;
            return this;
        }

        public Builder setConsumerSecret(String consumerSecret) {
            this.consumerSecret = consumerSecret;
            return this;
        }

        public Builder setMediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
            return this;
        }

        public void build() {
            APP_URL = appUrl;
            ACCESS_TOKEN = accessToken;
            CONSUMER_KEY = consumerKey;
            CONSUMER_SECRET = consumerSecret;
            MEDIA_URL_PATH = mediaUrl;
        }
    }

    public static String getAppUrl() {
        return APP_URL;
    }

    public static String getAccessToken() {
        return ACCESS_TOKEN;
    }

    public static String getConsumerKey() {
        return CONSUMER_KEY;
    }

    public static String getConsumerSecret() {
        return CONSUMER_SECRET;
    }
}
