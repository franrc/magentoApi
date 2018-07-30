package com.dekalabs.magentorestapi;

import android.content.Context;
import android.util.Log;

import com.dekalabs.magentorestapi.config.MagentoRestConfiguration;
import com.dekalabs.magentorestapi.dto.MagentoListResponse;
import com.dekalabs.magentorestapi.dto.MagentoResponse;
import com.dekalabs.magentorestapi.pojo.Category;
import com.dekalabs.magentorestapi.pojo.CategoryViews;
import com.dekalabs.magentorestapi.pojo.CustomAttribute;
import com.dekalabs.magentorestapi.pojo.Customer;
import com.dekalabs.magentorestapi.pojo.Product;
import com.dekalabs.magentorestapi.utils.DatabaseUtils;
import com.dekalabs.magentorestapi.utils.FilterOptions;
import com.dekalabs.magentorestapi.utils.PreferencesCacheManager;

import java.io.IOException;
import java.util.List;
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

    public void getCategoryList(Long root, ServiceCallbackOnlyOnServiceResults<List<Category>> callback) {

        final Long categoryRoot = root == null ? MagentoRestConfiguration.getRootCategoryId() : root;

        if (PreferencesCacheManager.getInstance().mustLoadCategoriesOf(categoryRoot)) {

            ServiceCallbackOnlyOnServiceResults<MagentoListResponse<Category>> firstCallback = new ServiceCallbackOnlyOnServiceResults<MagentoListResponse<Category>>() {
                @Override
                public void onResults(MagentoListResponse<Category> results) {
                    if(results == null) return;

                    if(results.getError() == null) {

                        DatabaseUtils.getInstance().saveCategories(categoryRoot, results.getItems());

                        callback.onResults(DatabaseUtils.getInstance().getCategoriesByParent(categoryRoot));

                        PreferencesCacheManager.getInstance().setCategoryCacheTime(categoryRoot);
                    }
                    else {
                        Log.e("MagentoRestService", "Error retrieving customAttributes: " + results.getError().getError());
                        callback.onError(-1, results.getError().getError());
                    }
                }

                @Override
                public void onError(int errorCode, String message) { callback.onError(errorCode, message); }

                @Override
                public void onFinish() { callback.onFinish(); }
            };

            Map<String, String> filterParams =
                    new FilterOptions()
                            .addFilter("include_in_menu", "1", FilterOptions.EQUALS)
                            .and()
                            .addFilter("parent_id", String.valueOf(categoryRoot), FilterOptions.EQUALS)
                            .build();

            executeListOnline(firstCallback, service.getCategoriesByParent(filterParams));

        }
        else {
            callback.onResults(DatabaseUtils.getInstance().getCategoriesByParent(categoryRoot));
            callback.onFinish();
        }
    }

    public void getCategoryDetail(Long categoryId, ServiceCallbackOnlyOnServiceResults<MagentoResponse<Category>> callback) {
        executeOnline(callback, service.getCategoryDetail(categoryId, null));
    }

    public void getProductsByCategory(Long categoryId, ServiceCallbackOnlyOnServiceResults<MagentoListResponse<Product>> callback) {

        final Long categoryID = categoryId == null ? MagentoRestConfiguration.getRootCategoryId() : categoryId;

        ServiceCallbackOnlyOnServiceResults<MagentoListResponse<Product>> firstCallback = new ServiceCallbackOnlyOnServiceResults<MagentoListResponse<Product>>() {
            @Override
            public void onResults(MagentoListResponse<Product> results) {
                if(results == null) return;

                if(results.getError() == null) {
                    DatabaseUtils.getInstance().saveProducts(categoryID, results.getItems());

                    MagentoListResponse<Product> magentoList = new MagentoListResponse<>();
                    magentoList.setItems(DatabaseUtils.getInstance().getProductsByCategory(categoryID));

                    callback.onResults(magentoList);
                }
                else {
                    Log.e("MagentoRestService", "Error retrieving products: " + results.getError().getError());
                }
            }

            @Override
            public void onError(int errorCode, String message) {
                callback.onError(errorCode, message);
            }

            @Override
            public void onFinish() {
                callback.onFinish();
            }
        };

        Map<String, String> queryString = new FilterOptions()
                                            .addFilter("category_id", categoryId != null ? String.valueOf(categoryId) : null, FilterOptions.EQUALS)
                                            .and()
                                            .addFilter("type_id", "configurable", FilterOptions.EQUALS)
                                            .build();

        executeListOnline(firstCallback, service.getProductsByCategory(queryString));
    }

    public void getProductsByCategoryView(Long categoryId, ServiceCallbackOnlyOnServiceResults<CategoryViews> callback) {

        final Long categoryID = categoryId == null ? MagentoRestConfiguration.getRootCategoryId() : categoryId;

        ServiceCallbackOnlyOnServiceResults<MagentoResponse<CategoryViews>> firstCallback = new ServiceCallbackOnlyOnServiceResults<MagentoResponse<CategoryViews>>() {
            @Override
            public void onResults(MagentoResponse<CategoryViews> results) {
                if(results == null) return;

                if(results.getError() == null) {
                    CategoryViews category = results.getData();

//                    if(category != null) {
//                        DatabaseUtils.getInstance().saveProducts(categoryID, category.getProductList());
//                    }

                    callback.onResults(category);
                }
                else {
                    Log.e("MagentoRestService", "Error retrieving products: " + results.getError().getError());
                }
            }

            @Override
            public void onError(int errorCode, String message) {
                callback.onError(errorCode, message);
            }

            @Override
            public void onFinish() {
                callback.onFinish();
            }
        };

        Map<String, String> queryString = new FilterOptions()
                .sort("position", FilterOptions.SORT_DIRECTION.ASC)
                .showFields("category[id,name],navigation[products[final_price,id,sku,name,type_id,extension_attributes,custom_attributes]]")
                .build();

        executeOnline(firstCallback, service.getProductsByCategoryView(categoryID, queryString));
    }

    public void getUserDefinedCustomAttributes(ServiceCallbackOnlyOnServiceResults<MagentoListResponse<CustomAttribute>> callback) {

        ServiceCallbackOnlyOnServiceResults<MagentoListResponse<CustomAttribute>> firstCallback = new ServiceCallbackOnlyOnServiceResults<MagentoListResponse<CustomAttribute>>() {
            @Override
            public void onResults(MagentoListResponse<CustomAttribute> results) {
                if(results == null) return;

                if(results.getError() == null) {
                    DatabaseUtils.getInstance().saveCustomAttributes(results.getItems());

                    MagentoListResponse<CustomAttribute> magentoList = new MagentoListResponse<>();
                    magentoList.setItems(DatabaseUtils.getInstance().getCustomAttributes());

                    callback.onResults(magentoList);
                }
                else {
                    Log.e("MagentoRestService", "Error retrieving customAttributes: " + results.getError().getError());
                }
            }

            @Override
            public void onError(int errorCode, String message) {
                callback.onError(errorCode, message);
            }

            @Override
            public void onFinish() {
                callback.onFinish();
            }
        };

        Map<String, String> queryString = new FilterOptions()
                .addFilter("is_user_defined", "1", FilterOptions.EQUALS)
                .showFields("items[attribute_id,attribute_code,options]")
                .build();


        executeListOnline(firstCallback, service.getAllCustomAttributes(queryString));
    }
}
