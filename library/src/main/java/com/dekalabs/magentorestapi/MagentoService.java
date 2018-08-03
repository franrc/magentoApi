package com.dekalabs.magentorestapi;

import com.dekalabs.magentorestapi.dto.CustomAttributeViewDTO;
import com.dekalabs.magentorestapi.dto.MagentoListResponse;
import com.dekalabs.magentorestapi.dto.MagentoResponse;
import com.dekalabs.magentorestapi.pojo.Category;
import com.dekalabs.magentorestapi.pojo.CategoryViews;
import com.dekalabs.magentorestapi.pojo.CustomAttribute;
import com.dekalabs.magentorestapi.pojo.Customer;
import com.dekalabs.magentorestapi.pojo.Product;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by fran on 9/7/18.
 */

public interface MagentoService {

//    @POST("auth/token/")
//    Call<AccessToken> refreshToken(@Body RefreshTokenDto request);
//
//    @POST("auth/token/")
//    Call<AccessToken> login(@Body LoginRequest request);

//    @POST("auth/token/destroy/")
//    Call<ResponseBody> unlogin();

    @GET("customers/me")
    Call<Customer> getMe();

    @PUT("customers/me")
    Call<ResponseBody> updateCustomer(@Body Customer customer);

    @GET("categories/list")
    Call<MagentoListResponse<Category>> getCategoriesByParent(@QueryMap Map<String, String> parameters);

    @GET("categories/{categoryId}")
    Call<Category> getCategoryDetail(@Query("categoryId") Long categoryId, @Query("storeId") Long storeId);

    @GET("products")
    Call<MagentoListResponse<Product>> getProductsByCategory(@QueryMap Map<String, String> parameters);

    @GET("category-views/{category_id}")
    Call<CategoryViews> getProductsByCategoryView(@Path("category_id") Long categoryId, @QueryMap Map<String, String> parameters);

    @GET("products/{sku}")
    Call<Product> getProductDetail(@Path("sku") String sku, @Query("storeId") Long storeId);

    @GET("products/attributes")
    Call<MagentoListResponse<CustomAttribute>> getAllCustomAttributes(@QueryMap Map<String, String> parameters);

    @GET("configurable-products/{sku}/children")
    Call<List<Product>> getConfigurableChildren(@Path("sku") String sku);

    @GET("product-views/{sku}")
    Call<CustomAttributeViewDTO> getProductView(@Path("sku") String sku, @QueryMap Map<String, String> parameters);

}
