package com.dekalabs.magentorestapi.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by dekalabs
 */
public class ServiceUtils {

    /**
     * Check if internet (wifi or 3g) is enabled on phone
     *
     * @return
     */
    public static boolean isInternetEnabled(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
