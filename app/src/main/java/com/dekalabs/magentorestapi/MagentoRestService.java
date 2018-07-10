package com.dekalabs.magentorestapi;

import android.content.Context;

import com.dekalabs.magentorestapi.config.MagentoRestConfiguration;
import com.dekalabs.magentorestapi.dto.MagentoListResponse;
import com.dekalabs.magentorestapi.dto.MagentoResponse;
import com.dekalabs.magentorestapi.pojo.Category;
import com.dekalabs.magentorestapi.pojo.Customer;
import com.dekalabs.magentorestapi.pojo.Product;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MagentoRestService extends DKRestService<MagentoService> {

    public MagentoRestService(Context context) {
        super(MagentoService.class);
        setCurrentContext(context);
    }

    @Override
    protected String getAppUrl() {
        return MagentoRestConfiguration.getAppUrl();
    }

    @Override
    protected void addInterceptors(OkHttpClient.Builder builder) {

        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

//                final AccessToken accessToken = AppSettings.getToken(currentContext);
//                String facebookAccessToken = AppSettings.getFacebookToken(currentContext);
//
//                if (accessToken != null && accessToken.getAccessToken() != null) {
//                    Request.Builder requestBuilder = original.newBuilder()
//                            .header("Accept", "application/json")
//                            .header("Content-type", "application/json")
//                            .header("Authorization",
//                                    accessToken.getTokenType() + " " + accessToken.getAccessToken())
//                            .method(original.method(), original.body());
//
//                    Request request = requestBuilder.build();
//                    return chain.proceed(request);
//                }
//                else if(facebookAccessToken != null){
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Content-type", "application/json")
                            .header("Authorization",
                                    "Bearer" + " " + MagentoRestConfiguration.getAccessToken())
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
//                }
//                else {
//                    return chain.proceed(original);
//                }
            }
        });
    }

//    public void refreshToken(String refreshToken, ServiceCallback<AccessToken> callback) {
//        RefreshTokenDto dto = new RefreshTokenDto();
//        dto.setRefreshToken(refreshToken);
//
//        executeOnline(callback, service.refreshToken(dto));
//    }
//
//    public void login(String email, String password, ServiceCallbackOnlyOnServiceResults<AccessToken> callback) {
//        LoginRequest request = new LoginRequest();
//        request.setUsername(email);
//        request.setPassword(password);
//
//        executeOnline(callback, service.login(request));
//    }
//
//
//    public void logout(ServiceCallbackOnlyOnServiceResults<ResponseBody> callback) {
//        executeOnline(callback, service.unlogin());
//    }

    public void getMe(ServiceCallbackOnlyOnServiceResults<MagentoResponse<Customer>> callback) {

        executeOnline(callback, service.getMe());
    }

    public void updateCustomer(Customer customer, ServiceCallbackOnlyOnServiceResults<MagentoResponse<ResponseBody>> callback) {

        executeOnline(callback, service.updateCustomer(customer));
    }

    public void getCategoryList(Long root, int depth, ServiceCallbackOnlyOnServiceResults<MagentoResponse<Category>> callback) {
        executeOnline(callback, service.getCategoriesByParent(root, depth));
    }

    public void getCategoryDetail(Long categoryId, ServiceCallbackOnlyOnServiceResults<MagentoResponse<Category>> callback) {
        executeOnline(callback, service.getCategoryDetail(categoryId, null));
    }

    public void getProductsByCategory(Long categoryId, ServiceCallbackOnlyOnServiceResults<MagentoListResponse<Product>> callback) {

        Map<String, String> queryString = new HashMap<>();
        queryString.put("searchCriteria[filterGroups][0][filters][0][field]", "parent_id");
        queryString.put("searchCriteria[filterGroups][0][filters][0][value]", categoryId != null ? String.valueOf(categoryId) : null);
        queryString.put("searchCriteria[filterGroups][0][filters][0][conditionType]", "eq");

        executeListOnline(callback, service.getProductsByCategory(queryString));
    }
}
