package com.dekalabs.magentorestapi.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.dekalabs.magentorestapi.BuildConfig;

public class MagentoSettings {

    public static final String TAG = "AppSettings";

    private static final String TOKEN = "CUSTOMER_TOKEN";

    public static void saveCustomerToken(Context context, String customerToken) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE).edit();
        prefs.putString(TOKEN, customerToken);
        prefs.commit();
    }

    public static String getCustomerToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        return prefs.getString(TOKEN, null);
    }
}