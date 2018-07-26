package com.dekalabs.magentorestapi;

import android.content.Context;
import android.util.Log;

/**
 * Created by dekalabs
 */
public abstract class ServiceCallback<DBType> {
     public abstract void onResults(DBType results);

    public void onError(Context context, String errorMessage){
        //ServiceUtils.showError(context, R.string.generic_error, android.R.string.ok, null);
        Log.e("DKRestService", errorMessage != null ? errorMessage : "Error not managed");
    }

    public void onError(int errorCode, String message) {
        Log.e("DKRestService", "ErrorCode: " + errorCode + " || " + message != null ? message : "Error not managed");
    }

    public void onNotInternetError() {
        DKRestService.notInternetError();
    }

    public void onFinish() {}
}
