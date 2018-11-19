package com.dekalabs.magentorestapi;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.dekalabs.magentorestapi.busevents.NotInternetException;
import com.dekalabs.magentorestapi.busevents.TokenException;
import com.dekalabs.magentorestapi.dto.MagentoError;
import com.dekalabs.magentorestapi.dto.MagentoListResponse;
import com.dekalabs.magentorestapi.dto.MagentoRegisterError;
import com.dekalabs.magentorestapi.dto.MagentoResponse;
import com.dekalabs.magentorestapi.utils.ServiceUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by dekalabs
 */
public abstract class DKRestService<IFSERVICE> {

    protected static boolean isOnDebug;

    public static void setIsOnDebug(boolean isDebug) {
        isOnDebug = isDebug;
    }

    protected Context currentContext;
        
    protected static ObjectMapper mapper = Jackson.DEFAULT_MAPPER.copy();

    protected OkHttpClient createClient() {
        OkHttpClient.Builder builder = null;

        if (isOnDebug) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


            builder = new OkHttpClient.Builder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .addInterceptor(interceptor);
        }
        else {
            builder = new OkHttpClient.Builder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS);
        }

        addInterceptors(builder);

        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();


            builder.sslSocketFactory(sslSocketFactory, new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            });
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        client = builder.build();

        return client;
    }

    protected OkHttpClient client;
    private Retrofit retrofit;

    protected IFSERVICE service;

    public DKRestService(Class<IFSERVICE> serviceClazz) {

        client = createClient();

        retrofit = new Retrofit.Builder()
                .baseUrl(getAppUrl())
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .build();

        service = retrofit.create(serviceClazz);
    }

    protected abstract String getAppUrl();
    protected abstract void addInterceptors(OkHttpClient.Builder builder);

    public void setCurrentContext(Context currentContext) {
        this.currentContext = currentContext;
    }

    public void changeUrl(String newUrl, Class<IFSERVICE> serviceClazz) {
        if(retrofit.baseUrl().toString().equals(newUrl))  return;

        retrofit = new Retrofit.Builder()
                .baseUrl(newUrl)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .build();

        service = retrofit.create(serviceClazz);
    }


    protected <ServerType> void executeOnline(final ServiceCallback<MagentoResponse<ServerType>> callback, Call<ServerType> call) {
        if (ServiceUtils.isInternetEnabled(currentContext)) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                call.enqueue(new Callback<ServerType>() {
                    @Override
                    public void onResponse(Call<ServerType> call, Response<ServerType> response) {
                        int responseCode = response.code();

                        if(responseCode >= 200 && responseCode <= 208) {
                            ServerType results = response.body();

                            MagentoResponse<ServerType> magentoResponse = new MagentoResponse<>();
                            magentoResponse.setData(results);

                            callback.onResults(magentoResponse);
                            callback.onFinish();
                        }
                        else if(response.code() == 401) {
                            EventBus.getDefault().post(new TokenException());
                            return;
                        }
                        else {
                            try {
                                MagentoError magentoError = Jackson.DEFAULT_MAPPER.readValue(response.errorBody().string(), MagentoError.class);
                                MagentoResponse<ServerType> magentoResponse = new MagentoResponse<>();
                                magentoResponse.setError(magentoError);

                                callback.onResults(magentoResponse);

                            } catch (IOException e) {
                                Log.e("DKRestService", "Parsing Magento Error: " + e.getMessage());
                                callback.onError(response.code(), response.message());
                            }
                            finally {
                                callback.onFinish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerType> call, Throwable t) {
                        callback.onError(currentContext, t.getMessage());
                        callback.onFinish();
                    }
                });
            } else {
                try {
                    Response<ServerType> response = call.execute();
                    ServerType results = response.body();

                    MagentoResponse<ServerType> magentoResponse = new MagentoResponse<>();
                    magentoResponse.setData(results);

                    callback.onResults(magentoResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                callback.onFinish();
            }
        } else {
            callback.onNotInternetError();
            callback.onFinish();
        }
    }

    protected <ServerType> void executeSimpleOnline(final ServiceCallback<ServerType> callback, Call<ServerType> call) {
        if (ServiceUtils.isInternetEnabled(currentContext)) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                call.enqueue(new Callback<ServerType>() {
                    @Override
                    public void onResponse(Call<ServerType> call, Response<ServerType> response) {
                        int responseCode = response.code();

                        if(responseCode >= 200 && responseCode <= 208) {
                            ServerType results = response.body();

                            callback.onResults(results);
                            callback.onFinish();
                        }
                        else {
                            try {
                                MagentoError magentoError = Jackson.DEFAULT_MAPPER.readValue(response.errorBody().string(), MagentoError.class);

                                if (magentoError != null)
                                    callback.onError(responseCode, magentoError.getError());
                                else
                                    callback.onError(response.code(), response.message());
                            } catch (IOException e) {
                                    try {
                                        MagentoRegisterError magentoRegError = Jackson.DEFAULT_MAPPER.readValue(response.body().toString(), MagentoRegisterError.class);
                                        callback.onError(responseCode, magentoRegError.getError());
                                    } catch (IOException e1) {
                                        Log.e("DKRestService", "Parsing Magento Error: " + e1.getMessage());
                                        callback.onError(response.code(), response.message());
                                    }

                            } finally {
                                callback.onFinish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerType> call, Throwable t) {
                        callback.onError(currentContext, t.getMessage());
                        callback.onFinish();
                    }
                });
            } else {
                try {
                    Response<ServerType> response = call.execute();
                    ServerType results = response.body();

                    callback.onResults(results);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                callback.onFinish();
            }
        } else {
            callback.onNotInternetError();
            callback.onFinish();
        }
    }

    protected <ServerType> void executeListOnline(final ServiceCallback<MagentoListResponse<ServerType>> callback, Call<MagentoListResponse<ServerType>> call) {
        if (ServiceUtils.isInternetEnabled(currentContext)) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                call.enqueue(new Callback<MagentoListResponse<ServerType>>() {
                    @Override
                    public void onResponse(Call<MagentoListResponse<ServerType>> call, Response<MagentoListResponse<ServerType>> response) {
                        int responseCode = response.code();

                        if(responseCode >= 200 && responseCode <= 208) {
                            MagentoListResponse<ServerType> results = response.body();

                            callback.onResults(results);
                            callback.onFinish();
                        }
                        else if(response.code() == 401) {
                            EventBus.getDefault().post(new TokenException());
                            return;
                        }
                        else {
                            try {
                                MagentoError magentoError = Jackson.DEFAULT_MAPPER.readValue(response.body().toString(), MagentoError.class);
                                MagentoListResponse<ServerType> magentoResponse = new MagentoListResponse<>();
                                magentoResponse.setError(magentoError);

                                callback.onResults(magentoResponse);

                            } catch (Exception e) {
                                    Log.e("DKRestService", "Parsing Magento Error: " + e1.getMessage());
                                    callback.onError(response.code(), response.message());
                            }
                            finally {
                                callback.onFinish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MagentoListResponse<ServerType>> call, Throwable t) {
                        callback.onError(currentContext, t.getMessage());
                        callback.onFinish();
                    }
                });
            } else {
                try {
                    Response<MagentoListResponse<ServerType>> response = call.execute();

                    int responseCode = response.code();

                    if(responseCode >= 200 && responseCode <= 208) {
                        MagentoListResponse<ServerType> results = response.body();

                        callback.onResults(results);
                        callback.onFinish();
                    }
                    else if(response.code() == 401) {
                        EventBus.getDefault().post(new TokenException());
                        return;
                    }
                    else {
                        try {
                            MagentoError magentoError = Jackson.DEFAULT_MAPPER.readValue(response.errorBody().toString(), MagentoError.class);
                            MagentoListResponse<ServerType> magentoResponse = new MagentoListResponse<>();
                            magentoResponse.setError(magentoError);

                            callback.onResults(magentoResponse);

                        } catch (IOException e) {
                            Log.e("DKRestService", "Parsing Magento Error: " + e.getMessage());
                            callback.onError(response.code(), response.message());
                        }
                        finally {
                            callback.onFinish();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                callback.onFinish();
            }
        } else {
            callback.onNotInternetError();
            callback.onFinish();
        }
    }


    protected <ServerType> MagentoResponse<ServerType> executeOnlineSync(Call<ServerType> call) {
        if (ServiceUtils.isInternetEnabled(currentContext)) {
            try {
                Response<ServerType> response = call.execute();

                int responseCode = response.code();

                if(responseCode >= 200 && responseCode <= 208) {
                    MagentoResponse<ServerType> results = new MagentoResponse<>();
                    results.setData(response.body());

                    return results;
                }
                else if(response.code() == 401) {
                    EventBus.getDefault().post(new TokenException());
                    return null;
                }
                else {
                    try {
                        MagentoError magentoError = Jackson.DEFAULT_MAPPER.readValue(response.body().toString(), MagentoError.class);
                        MagentoResponse<ServerType> magentoResponse = new MagentoResponse<>();
                        magentoResponse.setError(magentoError);

                        return magentoResponse;

                    } catch (IOException e) {
                        Log.e("DKRestService", "Parsing Magento Error: " + e.getMessage());
                        return null;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            DKRestService.notInternetError();
        }

        return null;
    }

    protected <ServerType> MagentoListResponse<ServerType> executeListOnlineSync(Call<MagentoListResponse<ServerType>> call) {
        if (ServiceUtils.isInternetEnabled(currentContext)) {
            try {
                Response<MagentoListResponse<ServerType>> response = call.execute();

                int responseCode = response.code();

                if(responseCode >= 200 && responseCode <= 208) {
                    return response.body();
                }
                else if(response.code() == 401) {
                    EventBus.getDefault().post(new TokenException());
                    return null;
                }
                else {
                    try {
                        MagentoError magentoError = Jackson.DEFAULT_MAPPER.readValue(response.body().toString(), MagentoError.class);
                        MagentoListResponse<ServerType> magentoResponse = new MagentoListResponse<>();
                        magentoResponse.setError(magentoError);

                        return magentoResponse;

                    } catch (IOException e) {
                        Log.e("DKRestService", "Parsing Magento Error: " + e.getMessage());
                        return null;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            DKRestService.notInternetError();
        }

        return null;
    }

    public static void notInternetError() {
        EventBus.getDefault().post(new NotInternetException());
    }
}