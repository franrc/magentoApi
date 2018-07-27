package com.dekalabs.magentorestapi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Date;

/**
 * Created by dekalabs on 02/08/2016
 */
public class PreferencesCacheManager {

    private final String CACHE_PREF_CATEGORY = "preferences.cache.category.";
    private final String CACHE_PREF_ATTR = "preferences.cache.category.attr";

    private static final Long CATEGORY_CACHE_TIME = 10800000L;  //3H
    private static final Long ATTRIBUTE_CACHE_TIME = 86400000L;  //24H

    private SharedPreferences sharedPreferences;

    private static PreferencesCacheManager sInstance;


    private PreferencesCacheManager(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void initialize(Context context) {
        sInstance = new PreferencesCacheManager(context);
    }

    public static PreferencesCacheManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("You must call PreferencesManager.initialize in the onCreate method of your custom Application");
        }

        return sInstance;
    }

    public void setCategoryCacheTime(Long categoryId){
        sharedPreferences.edit().putLong(CACHE_PREF_CATEGORY + categoryId, new Date().getTime()).apply();
    }

    public boolean mustLoadCategoriesOf(Long categoryId){
        Long oldTimestamp = sharedPreferences.getLong(CACHE_PREF_CATEGORY + categoryId,  0);

        Long currentTimestamp = new Date().getTime();

        return (currentTimestamp - oldTimestamp) > CATEGORY_CACHE_TIME;
    }


    public void setAttrCacheTime(){
        sharedPreferences.edit().putLong(CACHE_PREF_ATTR, new Date().getTime()).apply();
    }

    public boolean mustLoadAttrs(){
        Long oldTimestamp = sharedPreferences.getLong(CACHE_PREF_ATTR,  0);

        Long currentTimestamp = new Date().getTime();

        return (currentTimestamp - oldTimestamp) > CATEGORY_CACHE_TIME;
    }

    public void clear(){
        sharedPreferences.edit()
                .clear()
                .apply();
    }
}
