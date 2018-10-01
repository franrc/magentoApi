package com.dekalabs.magentorestapi;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dekalabs.magentorestapi.config.MagentoRestConfiguration;
import com.dekalabs.magentorestapi.dto.CustomAttributeViewDTO;
import com.dekalabs.magentorestapi.dto.Filter;
import com.dekalabs.magentorestapi.dto.MagentoListResponse;
import com.dekalabs.magentorestapi.dto.MagentoResponse;
import com.dekalabs.magentorestapi.dto.Pagination;
import com.dekalabs.magentorestapi.dto.ProductSearchDTO;
import com.dekalabs.magentorestapi.dto.ProductView;
import com.dekalabs.magentorestapi.dto.ReviewPost;
import com.dekalabs.magentorestapi.dto.ReviewResponseDTO;
import com.dekalabs.magentorestapi.pojo.Category;
import com.dekalabs.magentorestapi.pojo.CategoryViews;
import com.dekalabs.magentorestapi.pojo.CustomAttribute;
import com.dekalabs.magentorestapi.pojo.Customer;
import com.dekalabs.magentorestapi.pojo.Product;
import com.dekalabs.magentorestapi.pojo.WishList;
import com.dekalabs.magentorestapi.pojo.review.ReviewItem;
import com.dekalabs.magentorestapi.utils.DatabaseUtils;
import com.dekalabs.magentorestapi.utils.FilterOptions;
import com.dekalabs.magentorestapi.utils.PreferencesCacheManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java8.util.stream.StreamSupport;
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
                        DatabaseUtils database = new DatabaseUtils();

                        database.saveCategories(categoryRoot, results.getItems());

                        callback.onResults(database.getCategoriesByParent(categoryRoot));

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
            callback.onResults(new DatabaseUtils().getCategoriesByParent(categoryRoot));
            callback.onFinish();
        }
    }

    public void getCategoryDetail(Long categoryId, ServiceCallbackOnlyOnServiceResults<MagentoResponse<Category>> callback) {
        executeOnline(callback, service.getCategoryDetail(categoryId, null));
    }

//    public void getProductsByCategory(Long categoryId, ServiceCallbackOnlyOnServiceResults<MagentoListResponse<Product>> callback) {
//
//        final Long categoryID = categoryId == null ? MagentoRestConfiguration.getRootCategoryId() : categoryId;
//
//        ServiceCallbackOnlyOnServiceResults<MagentoListResponse<Product>> firstCallback = new ServiceCallbackOnlyOnServiceResults<MagentoListResponse<Product>>() {
//            @Override
//            public void onResults(MagentoListResponse<Product> results) {
//                if(results == null) return;
//
//                if(results.getError() == null) {
//                    DatabaseUtils database = new DatabaseUtils();
//                    database.saveProducts(categoryID, results.getItems());
//
//                    MagentoListResponse<Product> magentoList = new MagentoListResponse<>();
//                    magentoList.setItems(database.getProductsByCategory(categoryID));
//
//                    callback.onResults(magentoList);
//                }
//                else {
//                    Log.e("MagentoRestService", "Error retrieving products: " + results.getError().getError());
//                }
//            }
//
//            @Override
//            public void onError(int errorCode, String message) {
//                callback.onError(errorCode, message);
//            }
//
//            @Override
//            public void onFinish() {
//                callback.onFinish();
//            }
//        };
//
//        Map<String, String> queryString = new FilterOptions()
//                                            .addFilter("category_id", categoryId != null ? String.valueOf(categoryId) : null, FilterOptions.EQUALS)
//                                            .and()
//                                            .addFilter("type_id", "configurable", FilterOptions.EQUALS)
//                                            .build();
//
//        executeListOnline(firstCallback, service.getProductsByCategory(queryString));
//    }

    public void getProductsByCategoryView(Long categoryId, ServiceCallbackOnlyOnServiceResults<CategoryViews> callback) {
        getProductsByCategoryView(categoryId, null, callback);
    }

    public void getProductsByCategoryView(Long categoryId, Pagination pagination, ServiceCallbackOnlyOnServiceResults<CategoryViews> callback) {
        getProductsByCategoryView(categoryId, pagination, "position", null, callback);
    }

    public void getProductsByCategoryView(Long categoryId, Pagination pagination, String sort, List<Filter> filters, ServiceCallbackOnlyOnServiceResults<CategoryViews> callback) {

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

                    new DatabaseUtils().checkProductFavourite(category.getProductList());

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

        FilterOptions filterOptions = new FilterOptions();

        if(filters != null) {
            filterOptions = filterOptions.applyClientFilter(filters).and();
        }

        Map<String, String> queryString = filterOptions
                .sort(sort, FilterOptions.SORT_DIRECTION.ASC)
                .showFields("category[id,name],navigation[category_size,products[final_price,id,sku,name,type_id,extension_attributes,custom_attributes],filters]")
                .addPagination(pagination)
                .build();

        executeOnline(firstCallback, service.getProductsByCategoryView(categoryID, queryString));
    }

    public void getUserDefinedCustomAttributes(ServiceCallbackOnlyOnServiceResults<MagentoListResponse<CustomAttribute>> callback) {

        ServiceCallbackOnlyOnServiceResults<MagentoListResponse<CustomAttribute>> firstCallback = new ServiceCallbackOnlyOnServiceResults<MagentoListResponse<CustomAttribute>>() {
            @Override
            public void onResults(MagentoListResponse<CustomAttribute> results) {
                if(results == null) return;

                if(results.getError() == null) {
                    DatabaseUtils database = new DatabaseUtils();
                    database.saveCustomAttributes(results.getItems());

                    MagentoListResponse<CustomAttribute> magentoList = new MagentoListResponse<>();
                    magentoList.setItems(database.getCustomAttributes());

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

//    public void getConfigurableProductChildren(String sku, ServiceCallback<List<Product>> callback) {
//        ServiceCallback<MagentoListResponse<Product>> firstCallback = new ServiceCallback<MagentoListResponse<Product>>() {
//            @Override
//            public void onResults(MagentoListResponse<Product> results) {
//                if(results == null) return;
//
//                if(results.getError() == null) {
//                    callback.onResults(results.getItems());
//                }
//                else {
//                    Log.e("MagentoRestService", "Error retrieving customAttributes: " + results.getError().getError());
//                    callback.onError(-1, results.getError().getError());
//                }
//            }
//
//            @Override
//            public void onError(int errorCode, String message) {
//                callback.onError(errorCode, message);
//            }
//
//            @Override
//            public void onFinish() {
//                callback.onFinish();
//            }
//        };
//
//        executeListOnline(firstCallback, service.getConfigurableChildren(sku));
//    }

    public void getProductDetail(String sku, ServiceCallback<ProductView> callback) {
        getProductDetail(sku, null, callback);
    }

    public void getProductDetail(String sku, Long storeId, ServiceCallback<ProductView> callback) {
        ServiceCallback<MagentoResponse<Product>> bySkuCallback = new ServiceCallback<MagentoResponse<Product>>() {
            @Override
            public void onResults(MagentoResponse<Product> results) {
                if(results == null) return;

                if(results.getError() == null) {

                    //Check if this product is simple or configurable. If it's simple, we can return it as normal
                    Product product = results.getData();

                    if(Product.TYPE_SIMPLE.equals(product.getTypeId())) {
                        //Check if its favourite
                        new DatabaseUtils().checkProductFavourite(product);

                        getProductViewFinalPrice(new ProductView(product), callback);
                    }
                    else {
                        //As type is configurable, we must retrieve the configurable attributes and this product children
                        getProductChildren(product, callback);
                    }
                }
                else {
                    Log.e("MagentoRestService", "Error retrieving product by sku: " + results.getError().getError());
                    callback.onError(-1, results.getError().getError());
                    callback.onFinish();
                }
            }

            @Override
            public void onError(int errorCode, String message) {
                callback.onError(errorCode, message);
                callback.onFinish();
            }
        };

        executeOnline(bySkuCallback, service.getProductDetail(sku, storeId));
    }

    private void getProductFinalPrice(Long productId, ServiceCallback<Product> callback) {

        Map<String, String> queryString = new FilterOptions()
                .showFields("id,final_price")
                .build();

        executeSimpleOnline(callback, service.getProductViewData(productId, queryString));
    }

    private void getProductViewFinalPrice(ProductView productView, ServiceCallback<ProductView> callback) {

        Map<String, String> queryString = new FilterOptions()
                .showFields("id,final_price")
                .build();

        ServiceCallback<Product> firstCallback = new ServiceCallback<Product>() {
            @Override
            public void onResults(Product results) {
                if(results == null) {
                    callback.onError(-1, "Error getting price");
                    callback.onFinish();

                    return;
                }

                productView.getMainProduct().setFinalPrice(results.getFinalPrice());
                callback.onResults(productView);
                callback.onFinish();
            }

            @Override
            public void onError(int errorCode, String message) {
                callback.onError(errorCode, message);
                callback.onFinish();
            }
        };

        executeSimpleOnline(firstCallback, service.getProductViewData(productView.getMainProduct().getId(), queryString));
    }


    private void getProductsFinalPrice(List<Product> products, ServiceCallback<List<Product>> callback) {
        List<Product> productsWithPrice = new ArrayList<>();

        StreamSupport.stream(products)
                .forEach(p -> {
                    getProductFinalPrice(p.getId(), new ServiceCallback<Product>() {
                        @Override
                        public void onResults(Product results) {
                            if(results != null) {
                                p.setFinalPrice(results.getFinalPrice());
                            }

                            productsWithPrice.add(p);
                        }

                        @Override
                        public void onError(int errorCode, String message) {
                            productsWithPrice.add(p);
                        }

                        @Override
                        public void onFinish() {
                            if(productsWithPrice.size() == products.size()) {
                                callback.onResults(productsWithPrice);
                                callback.onFinish();
                            }
                        }
                    });
                });
    }

    public void getProductIdsFinalPrice(List<Long> productIds, ServiceCallback<List<Product>> callback) {
        List<Product> productsWithPrice = new ArrayList<>();

        StreamSupport.stream(productIds)
                .forEach(pId -> {
                    getProductFinalPrice(pId, new ServiceCallback<Product>() {
                        @Override
                        public void onResults(Product results) {
                            productsWithPrice.add(results);
                        }

                        @Override
                        public void onError(int errorCode, String message) {
                            productsWithPrice.add(new Product(pId));
                        }

                        @Override
                        public void onFinish() {
                            if(productsWithPrice.size() == productIds.size()) {
                                callback.onResults(productsWithPrice);
                                callback.onFinish();
                            }
                        }
                    });
                });
    }


    private void getProductChildren(Product product, ServiceCallback<ProductView> callback) {

        //At this point, we will retrieve product children and, after that, we will ask the server for the configurable attributes by product-view service

        ServiceCallback<MagentoResponse<List<Product>>> childrenCallback = new ServiceCallback<MagentoResponse<List<Product>>>() {
            @Override
            public void onResults(MagentoResponse<List<Product>> results) {
                if(results == null) return;

                if(results.getError() == null) {

                    getProductsFinalPrice(results.getData(), new ServiceCallback<List<Product>>() {
                        @Override
                        public void onResults(List<Product> pricedProducts) {
                            //Now we have the product children, with the configurable list
                            ProductView productView = new ProductView(product);
                            productView.setChildren(pricedProducts);

                            //Check if it's needed to mark as favourite
                            new Handler(Looper.getMainLooper()).post(() -> new DatabaseUtils().markProductViewAsFavourite(productView));

                            //Let's go for the attributes!
                            getConfigurableProductAttributes(productView, callback);
                        }
                    });
                }
                else {
                    Log.e("MagentoRestService", "Error retrieving children products: " + results.getError().getError());
                    callback.onError(-1, results.getError().getError());
                    callback.onFinish();
                }
            }

            @Override
            public void onError(int errorCode, String message) {
                callback.onError(errorCode, message);
                callback.onFinish();
            }
        };

        executeOnline(childrenCallback, service.getConfigurableChildren(product.getSku()));
    }

    private void getConfigurableProductAttributes(ProductView productView, ServiceCallback<ProductView> callback) {
        ServiceCallback<MagentoResponse<CustomAttributeViewDTO>> childrenCallback = new ServiceCallback<MagentoResponse<CustomAttributeViewDTO>>() {
            @Override
            public void onResults(MagentoResponse<CustomAttributeViewDTO> results) {
                if(results == null) return;

                if(results.getError() == null) {

                    //Now we have the product children, with the configurable list
                    productView.setAttributes(results.getData().getAttributeViews());

                    callback.onResults(productView);
                    callback.onFinish();
                }
                else {
                    Log.e("MagentoRestService", "Error retrieving children products: " + results.getError().getError());
                    callback.onError(-1, results.getError().getError());
                    callback.onFinish();
                }
            }

            @Override
            public void onError(int errorCode, String message) {
                callback.onError(errorCode, message);
                callback.onFinish();
            }
        };

        Map<String, String> queryString = new FilterOptions()
                .showFields("configurable_product_options")
                .build();

        executeOnline(childrenCallback, service.getProductView(productView.getMainProduct().getSku(), queryString));
    }

    public void getBasicProductDataBySkuList(List<String> skuList, ServiceCallback<List<Product>> callback) {

        ServiceCallback<MagentoListResponse<Product>> firstCallback = new ServiceCallback<MagentoListResponse<Product>>() {
            @Override
            public void onResults(MagentoListResponse<Product> results) {
                if(results == null) return;

                if(results.getError() == null) {
                    callback.onResults(results.getItems());
                    callback.onFinish();
                }
                else {
                    Log.e("MagentoRestService", "Error retrieving products by sku: " + results.getError().getError());
                    callback.onError(-1, results.getError().getError());
                    callback.onFinish();
                }
            }

            @Override
            public void onError(int errorCode, String message) {
                callback.onError(errorCode, message);
                callback.onFinish();
            }
        };


        Map<String, String> queryString = new FilterOptions()
                .addFilter("sku", android.text.TextUtils.join(",", skuList), FilterOptions.IN)
                .showFields("items[id,sku,price]")
                .build();

        executeListOnline(firstCallback, service.getProductBasicDataBySkuList(queryString));
    }

    public void getProductReviews(Long productID, ServiceCallback<List<ReviewItem>> callback) {

        ServiceCallback<MagentoResponse<List<ReviewItem>>> firstCallback = new ServiceCallback<MagentoResponse<List<ReviewItem>>>() {
            @Override
            public void onResults(MagentoResponse<List<ReviewItem>> results) {
                if(results == null) return;

                if(results.getError() == null) {
                    callback.onResults(results.getData());
                    callback.onFinish();
                }
                else {
                    Log.e("MagentoRestService", "Error retrieving Reviews of product: " + results.getError().getError());
                    callback.onError(-1, results.getError().getError());
                    callback.onFinish();
                }
            }

            @Override
            public void onError(int errorCode, String message) {
                callback.onError(errorCode, message);
                callback.onFinish();
            }
        };


        Map<String, String> queryString = new FilterOptions()
                .sort("created_at", FilterOptions.SORT_DIRECTION.DESC)
                .build();

        executeOnline(firstCallback, service.getProductReviews(productID, queryString));
    }

    public void sendGuestReview(ReviewPost reviewPost, ServiceCallback<ReviewResponseDTO> callback) {
        executeSimpleOnline(callback, service.sendGuestReview(reviewPost));
    }


    public void addProductToWishList(String productSku, ServiceCallback<Boolean> callback) {
        new DatabaseUtils().addProductToWishList(productSku);

        callback.onResults(true);
        callback.onFinish();
    }

    public void removeProductFromWishList(String productSku, ServiceCallback<Boolean> callback) {
        callback.onResults(new DatabaseUtils().removeProductFromWishList(productSku));
        callback.onFinish();
    }

    public void getWishList(ServiceCallback<WishList> callback) {
        WishList wishList = new DatabaseUtils().getWishList();

        if(wishList == null || wishList.getProducts().size() == 0) {
            callback.onResults(wishList);
            callback.onFinish();
        }
        else {
            StreamSupport.stream(wishList.getProducts()).parallel()
                    .forEach(sku -> {
                        getProductDetail(sku, new ServiceCallback<ProductView>() {
                            @Override
                            public void onResults(ProductView results) {
                                wishList.getProductList().add(results.getMainProduct());  //Always main product cause they are always children
                            }

                            @Override
                            public void onFinish() {
                                if(wishList.getProducts().size() == wishList.getProductList().size()) {
                                    callback.onResults(wishList);
                                    callback.onFinish();
                                }
                            }
                        });
                    });
        }
    }

    /** Search **/
    public void searchProducts(String query, Pagination pagination, String order, List<Filter> filters, ServiceCallbackOnlyOnServiceResults<ProductSearchDTO> callback) {

        ServiceCallbackOnlyOnServiceResults<ProductSearchDTO> firstCallback = new ServiceCallbackOnlyOnServiceResults<ProductSearchDTO>() {
            @Override
            public void onResults(ProductSearchDTO results) {
                if (results == null) return;
//                    if(category != null) {
//                        DatabaseUtils.getInstance().saveProducts(categoryID, category.getProductList());
//                    }

                new DatabaseUtils().checkProductFavourite(results.getProducts());

                callback.onResults(results);
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

        order = order != null ? order : "position";

        FilterOptions filterOptions = new FilterOptions();

        if(filters != null) {
            filterOptions = filterOptions.applyClientFilter(filters).and();
        }

        Map<String, String> queryString = filterOptions
                .sort(order, FilterOptions.SORT_DIRECTION.ASC)
                .showFields("products,filters,category_size")
                .addPagination(pagination)
                .build();

        queryString.put("query", query);

        executeSimpleOnline(firstCallback, service.searchProducts(queryString));
    }
}
