package com.dekalabs.magentorestapi;

import com.dekalabs.magentorestapi.dto.AddressDTO;
import com.dekalabs.magentorestapi.dto.Block;
import com.dekalabs.magentorestapi.dto.CartItemDto;
import com.dekalabs.magentorestapi.dto.CountryRegion;
import com.dekalabs.magentorestapi.dto.CustomAttributeViewDTO;
import com.dekalabs.magentorestapi.dto.CustomerEmailCheckerDTO;
import com.dekalabs.magentorestapi.dto.CustomerLoginDTO;
import com.dekalabs.magentorestapi.dto.CustomerRegisterDTO;
import com.dekalabs.magentorestapi.dto.DeliveryNotesDto;
import com.dekalabs.magentorestapi.dto.MagentoListResponse;
import com.dekalabs.magentorestapi.dto.PlaceOrderDTO;
import com.dekalabs.magentorestapi.dto.ProductSearchDTO;
import com.dekalabs.magentorestapi.dto.ReviewPost;
import com.dekalabs.magentorestapi.dto.ReviewResponseDTO;
import com.dekalabs.magentorestapi.dto.ShippingAddressDTO;
import com.dekalabs.magentorestapi.pojo.Address;
import com.dekalabs.magentorestapi.pojo.Category;
import com.dekalabs.magentorestapi.pojo.CategoryViews;
import com.dekalabs.magentorestapi.pojo.CustomAttribute;
import com.dekalabs.magentorestapi.pojo.Customer;
import com.dekalabs.magentorestapi.pojo.Product;
import com.dekalabs.magentorestapi.pojo.cart.CartItem;
import com.dekalabs.magentorestapi.pojo.cart.CartTotals;
import com.dekalabs.magentorestapi.pojo.cart.PaymentMethod;
import com.dekalabs.magentorestapi.pojo.cart.ShippingMethod;
import com.dekalabs.magentorestapi.pojo.cart.ShoppingCart;
import com.dekalabs.magentorestapi.pojo.review.ReviewItem;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

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

    @GET("products")
    Call<MagentoListResponse<Product>> getProductBasicDataBySkuList(@QueryMap Map<String, String> params);

    @POST("review/guest/post")
    Call<ReviewResponseDTO> sendGuestReview(@Body ReviewPost review);

    @POST("review/mine/post")
    Call<ReviewResponseDTO> sendCustomerReview(@Body ReviewPost review);

    @GET("review/reviews/{productId}")
    Call<List<ReviewItem>> getProductReviews(@Path("productId") Long productId, @QueryMap Map<String, String> params);

    @GET("product-views/id/{id}")
    Call<Product> getProductViewData(@Path("id") Long productId, @QueryMap Map<String, String> params);

    @GET("search-views")
    Call<ProductSearchDTO> searchProducts(@QueryMap Map<String, String> params);

    @GET("spa/product-views/wjh_barcode/lookup/{barcode}")
    Call<Product> searchProductByBarcode(@Path("barcode") String barcode);

    @GET("spa/cmsBlock/identifier")
    Call<Block> getRenderBlock(@Query("identifier") String identifier);


    /*** Checkout ***/
    /****** GUEST ****/

    @POST("guest-carts")
    Call<String> createGuestCart();

    @POST("carts/mine")
    Call<String> createCustomerCart();

    @GET("guest-carts/{cartIdentifier}")
    Call<ShoppingCart> getGuestShoppingCartByIdentifier(@Path("cartIdentifier") String id);

    @GET("carts/mine")
    Call<ShoppingCart> getCustomerShoppingCart();

    @POST("guest-carts/{cartIdentifier}/estimate-shipping-methods")
    Call<List<ShippingMethod>> getGuestShippingMethods(@Path("cartIdentifier") String id, @Body AddressDTO address);

    @POST("carts/mine/estimate-shipping-methods")
    Call<List<ShippingMethod>> getCustomerShippingMethods(@Body AddressDTO address);

    @GET("guest-carts/{cartIdentifier}/payment-methods")
    Call<List<PaymentMethod>> getGuestPaymentMethods(@Path("cartIdentifier") String cartId);

    @GET("carts/mine/payment-methods")
    Call<List<PaymentMethod>> getCustomerPaymentMethods();

    @GET
    Call<ResponseBody> executeUrl(@Url String url);

    @POST("guest-carts/{cartIdentifier}/items")
    Call<CartItem> addItemToGuestCart(@Path("cartIdentifier") String cartId, @Body CartItemDto cartItem);

    @POST("carts/mine/items")
    Call<CartItem> addItemToCustomerCart(@Body CartItemDto cartItem);

    @POST("guest-carts/{cartIdentifier}/shipping-information")
    Call<ShoppingCart> getGuestShippingInformation(@Path("cartIdentifier") String id, @Body ShippingAddressDTO dto);

    @POST("carts/mine/shipping-information")
    Call<ShoppingCart> getCustomerShippingInformation(@Body ShippingAddressDTO dto);

    @GET("guest-carts/{cartIdentifier}/totals")
    Call<CartTotals> getGuestCartTotals(@Path("cartIdentifier") String id);

    @GET("carts/mine/totals")
    Call<CartTotals> getCustomerCartTotals();

    @PUT("guest-carts/{cartIdentifier}/coupons/{coupon}")
    Call<Boolean> applyGuestCoupon(@Path("cartIdentifier") String cartId, @Path("coupon") String coupon);

    @PUT("carts/mine/coupons/{coupon}")
    Call<Boolean> applyCustomerCoupon(@Path("coupon") String coupon);

    @POST("customers/isEmailAvailable")
    Call<Boolean> isEmailAvailable(@Body CustomerEmailCheckerDTO dto);

    @POST("guest-carts/{cartIdentifier}/billing-address")
    Call<String> postGuestBillingAddress(@Path("cartIdentifier") String cartId, @Body Address billingAddress);

    @POST("carts/mine/billing-address")
    Call<String> postCustomerBillingAddress(@Body Address billingAddress);

    @PUT("guest-carts/{cartIdentifier}/items/{itemId}")
    Call<CartItem> updateGuestCartItem(@Path("cartIdentifier") String cartId, @Path("itemId") Long itemId, @Body CartItemDto dto);

    @PUT("carts/mine/items/{itemId}")
    Call<CartItem> updateCustomerCartItem(@Path("itemId") Long itemId, @Body CartItemDto dto);

    @DELETE("guest-carts/{cartIdentifier}/items/{itemId}")
    Call<Boolean> deleteGuestCartItem(@Path("cartIdentifier") String cartId, @Path("itemId") Long itemId);

    @DELETE("carts/mine/items/{itemId}")
    Call<Boolean> deleteCustomerCartItem(@Path("itemId") Long itemId);

    @PUT("guest-carts/{cartIdentifier}/set-order-comment")
    Call<ResponseBody> setGuestDeliveryNotes(@Path("cartIdentifier") String cartId, @Body DeliveryNotesDto dto);

    @PUT("carts/mine/set-order-comment")
    Call<ResponseBody> setCustomerDeliveryNotes(@Body DeliveryNotesDto dto);

    @POST("guest-carts/{cartIdentifier}/payment-information")
    Call<String> placeGuestOrder(@Path("cartIdentifier") String cartId, @Body PlaceOrderDTO dto);

    @POST("carts/me/payment-information")
    Call<String> placeCustomerOrder(@Body PlaceOrderDTO dto);

    @GET("directory/countries")
    Call<List<CountryRegion>> getCountries();


    /*** CUSTOMER ***/
    @POST("customers/")
    Call<Customer> registerCustomer(@Body CustomerRegisterDTO body);

    @POST("integration/customer/token")
    Call<String> loginCustomer(@Body CustomerLoginDTO dto);

    @GET("customers/me")
    Call<Customer> getCurrentCustomerData();

    @GET("customers/me/shippingAddress")
    Call<List<Address>> getCustomerShippingAddresses();

    @GET("customers/me/billingAddress")
    Call<List<Address>> getCustomerBillingAddresses();

}
