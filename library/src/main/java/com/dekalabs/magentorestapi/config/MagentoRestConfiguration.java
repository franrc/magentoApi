package com.dekalabs.magentorestapi.config;

import android.content.Context;
import android.util.Log;

import com.dekalabs.magentorestapi.MagentoRestService;
import com.dekalabs.magentorestapi.ServiceCallbackOnlyOnServiceResults;
import com.dekalabs.magentorestapi.dto.MagentoListResponse;
import com.dekalabs.magentorestapi.handler.FinishHandler;
import com.dekalabs.magentorestapi.pojo.CustomAttribute;
import com.dekalabs.magentorestapi.utils.PreferencesCacheManager;

import java.net.MalformedURLException;
import java.net.URL;

import io.realm.Realm;

public class MagentoRestConfiguration {

    private static String APP_URL;
    private static String PRODUCT_MEDIA_URL_PATH;
    private static String CATEGORY_MEDIA_URL_PATH;
    private static String ACCESS_TOKEN;
    private static String CONSUMER_KEY;
    private static String CONSUMER_SECRET;
    private static Long ROOT_CATEGORY_ID;


    public static class Builder {

        private String appUrl;
        private String accessToken;
        private String consumerKey;
        private String consumerSecret;
        private String productMediaUrl;
        private String categoryMediaUrl;
        private Long rootCategoryId;

        Context context;

        public Builder(Context context) {
            this.context = context;

            PreferencesCacheManager.initialize(context);
        }

        public Builder setAppUrl(String appUrl) {
            this.appUrl = appUrl;

            if(productMediaUrl == null) {
                try {
                    URL url = new URL(appUrl);

                    productMediaUrl = url.getProtocol() + "://" + url.getHost() + "/media/catalog/product/";

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

            if(categoryMediaUrl == null) {
                try {
                    URL url = new URL(appUrl);

                    categoryMediaUrl = url.getProtocol() + "://" + url.getHost() + "/media/catalog/category/";

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

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

        public Builder setProductMediaUrl(String mediaUrl) {
            this.productMediaUrl = mediaUrl;
            return this;
        }

        public Builder setCategoryMediaUrl(String mediaUrl) {
            this.categoryMediaUrl = mediaUrl;
            return this;
        }

        public Builder setRootCategoryId(Long rootCategoryId) {
            this.rootCategoryId = rootCategoryId;
            return this;
        }

        public Builder forceReloadData() {
            PreferencesCacheManager.getInstance().clear();
            return this;
        }

        public void build(FinishHandler finishHandler) {
            if(finishHandler == null) throw new IllegalArgumentException("FinishHandler could not be null");

            if(appUrl != null)
                APP_URL = appUrl;

            if(accessToken != null)
                ACCESS_TOKEN = accessToken;

            if(consumerKey != null)
                CONSUMER_KEY = consumerKey;

            if(consumerSecret != null)
                CONSUMER_SECRET = consumerSecret;

            if(productMediaUrl != null)
                PRODUCT_MEDIA_URL_PATH = productMediaUrl;

            if(categoryMediaUrl != null)
                CATEGORY_MEDIA_URL_PATH = categoryMediaUrl;

            if(rootCategoryId != null)
                ROOT_CATEGORY_ID = rootCategoryId;

            Realm.init(context);

            if (PreferencesCacheManager.getInstance().mustLoadAttrs()) {
                new MagentoRestService(context).getUserDefinedCustomAttributes(new ServiceCallbackOnlyOnServiceResults<MagentoListResponse<CustomAttribute>>() {
                    @Override
                    public void onResults(MagentoListResponse<CustomAttribute> results) {
                        PreferencesCacheManager.getInstance().setAttrCacheTime();

                        Log.i("Sample", (results != null ? String.valueOf(results.getItems().size()) : "vacío"));
                        finishHandler.onFinish();
                    }
                });
            }
            else {
                finishHandler.onFinish();
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

    public static String getProductMediaUrlPath() {
        return PRODUCT_MEDIA_URL_PATH;
    }

    public static String getCategoryMediaUrlPath() {
        return CATEGORY_MEDIA_URL_PATH;
    }
}
