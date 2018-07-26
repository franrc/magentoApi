package com.dekalabs.magentorestapi.config;

import android.content.Context;
import android.util.Log;

import com.dekalabs.magentorestapi.MagentoRestService;
import com.dekalabs.magentorestapi.ServiceCallbackOnlyOnServiceResults;
import com.dekalabs.magentorestapi.dto.MagentoListResponse;
import com.dekalabs.magentorestapi.pojo.CustomAttribute;
import com.dekalabs.magentorestapi.utils.PreferencesManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MagentoRestConfiguration {

    private static String APP_URL;
    private static String MEDIA_URL_PATH;
    private static String ACCESS_TOKEN;
    private static String CONSUMER_KEY;
    private static String CONSUMER_SECRET;
    private static Long ROOT_CATEGORY_ID;


    public static class Builder {

        private String appUrl;
        private String accessToken;
        private String consumerKey;
        private String consumerSecret;
        private String mediaUrl;
        private Long rootCategoryId;

        Context context;

        public Builder(Context context) {
            this.context = context;
        }

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

        public Builder setRootCategoryId(Long rootCategoryId) {
            this.rootCategoryId = rootCategoryId;
            return this;
        }

        public void build() {
            APP_URL = appUrl;
            ACCESS_TOKEN = accessToken;
            CONSUMER_KEY = consumerKey;
            CONSUMER_SECRET = consumerSecret;
            MEDIA_URL_PATH = mediaUrl;
            ROOT_CATEGORY_ID = rootCategoryId;

            PreferencesManager.initialize(context);

            Realm.init(context);
            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                    .schemaVersion(0)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            Realm.setDefaultConfiguration(realmConfiguration);

            if (PreferencesManager.getInstance().mustLoadAttrs()) {
                new MagentoRestService(context).getUserDefinedCustomAttributes(new ServiceCallbackOnlyOnServiceResults<MagentoListResponse<CustomAttribute>>() {
                    @Override
                    public void onResults(MagentoListResponse<CustomAttribute> results) {
                        PreferencesManager.getInstance().setAttrCacheTime();

                        Log.i("Sample", (results != null ? String.valueOf(results.getItems().size()) : "vacío"));
                    }
                });
            }
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

    public static Long getRootCategoryId() {
        return ROOT_CATEGORY_ID;
    }
}
