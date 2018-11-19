package com.dekalabs.magentorestapi;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dekalabs.magentorestapi.config.MagentoRestConfiguration;
import com.dekalabs.magentorestapi.config.MagentoSettings;
import com.dekalabs.magentorestapi.dto.AddressDTO;
import com.dekalabs.magentorestapi.dto.Block;
import com.dekalabs.magentorestapi.dto.CartItemDto;
import com.dekalabs.magentorestapi.dto.CountryRegion;
import com.dekalabs.magentorestapi.dto.CustomAttributeViewDTO;
import com.dekalabs.magentorestapi.dto.CustomerEmailCheckerDTO;
import com.dekalabs.magentorestapi.dto.CustomerLoginDTO;
import com.dekalabs.magentorestapi.dto.CustomerRegisterDTO;
import com.dekalabs.magentorestapi.dto.DeliveryNotesDto;
import com.dekalabs.magentorestapi.dto.Filter;
import com.dekalabs.magentorestapi.dto.MagentoListResponse;
import com.dekalabs.magentorestapi.dto.MagentoResponse;
import com.dekalabs.magentorestapi.dto.Pagination;
import com.dekalabs.magentorestapi.dto.PaymentAssignementMethodDTO;
import com.dekalabs.magentorestapi.dto.PlaceOrderDTO;
import com.dekalabs.magentorestapi.dto.ProductSearchDTO;
import com.dekalabs.magentorestapi.dto.ProductView;
import com.dekalabs.magentorestapi.dto.ReviewPost;
import com.dekalabs.magentorestapi.dto.ReviewResponseDTO;
import com.dekalabs.magentorestapi.dto.ShippingAddressDTO;
import com.dekalabs.magentorestapi.handler.FinishHandler;
import com.dekalabs.magentorestapi.pojo.Address;
import com.dekalabs.magentorestapi.pojo.Category;
import com.dekalabs.magentorestapi.pojo.CategoryViews;
import com.dekalabs.magentorestapi.pojo.CustomAttribute;
import com.dekalabs.magentorestapi.pojo.Customer;
import com.dekalabs.magentorestapi.pojo.Product;
import com.dekalabs.magentorestapi.pojo.WishList;
import com.dekalabs.magentorestapi.pojo.cart.CartItem;
import com.dekalabs.magentorestapi.pojo.cart.CartTotals;
import com.dekalabs.magentorestapi.pojo.cart.PaymentMethod;
import com.dekalabs.magentorestapi.pojo.cart.ShippingMethod;
import com.dekalabs.magentorestapi.pojo.cart.ShoppingCart;
import com.dekalabs.magentorestapi.pojo.review.ReviewItem;
import com.dekalabs.magentorestapi.utils.FilterOptions;
import com.dekalabs.magentorestapi.utils.FinalInteger;
import com.dekalabs.magentorestapi.utils.MagentoDatabaseUtils;
import com.dekalabs.magentorestapi.utils.PreferencesCacheManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import java8.util.stream.StreamSupport;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;

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

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Content-type", "application/json")
                            .header("Authorization",
                                    "Bearer" + " " + MagentoRestConfiguration.getAccessToken())
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
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
                        MagentoDatabaseUtils database = new MagentoDatabaseUtils();

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
            callback.onResults(new MagentoDatabaseUtils().getCategoriesByParent(categoryRoot));
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

                    new MagentoDatabaseUtils().checkProductFavourite(category.getProductList());

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
                    MagentoDatabaseUtils database = new MagentoDatabaseUtils();
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
                        new MagentoDatabaseUtils().checkProductFavourite(product);

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
                            new Handler(Looper.getMainLooper()).post(() -> new MagentoDatabaseUtils().markProductViewAsFavourite(productView));

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
        new MagentoDatabaseUtils().addProductToWishList(productSku);

        callback.onResults(true);
        callback.onFinish();
    }

    public void removeProductFromWishList(String productSku, ServiceCallback<Boolean> callback) {
        callback.onResults(new MagentoDatabaseUtils().removeProductFromWishList(productSku));
        callback.onFinish();
    }

    public void getWishList(ServiceCallback<WishList> callback) {
        WishList wishList = new MagentoDatabaseUtils().getWishList();

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

                new MagentoDatabaseUtils().checkProductFavourite(results.getProducts());

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

    public void searchProductByBarcode(String barcode, ServiceCallbackOnlyOnServiceResults<Product> callback) {
        executeSimpleOnline(callback, service.searchProductByBarcode(barcode));
    }

    public void renderBlock(String blockIdentifier, ServiceCallback<String> callback) {

        ServiceCallback<Block> managerCallback = new ServiceCallback<Block>() {
            @Override
            public void onResults(Block results) {
                if (results == null) return;

                callback.onResults(results.getContent());
                callback.onFinish();
            }

            @Override
            public void onError(int errorCode, String message) {
                callback.onError(errorCode, message);
                callback.onFinish();
            }
        };

        executeSimpleOnline(managerCallback, service.getRenderBlock(blockIdentifier));
    }


    /**** CHECKOUT *****/
    /**** GUEST *****/

//    public void getGuestCart(ServiceCallbackOnlyOnServiceResults<ShoppingCart> callback) {
//        ShoppingCart currentCart = new MagentoDatabaseUtils().retrieveCart();
//
//        if(currentCart == null) {
//            createGuestCart(new ServiceCallbackOnlyOnServiceResults<String>() {
//                @Override
//                public void onResults(String results) {
//                    findGuestCartByIds(results, callback);
//                }
//
//                @Override
//                public void onError(int errorCode, String message) {
//                    callback.onError(errorCode, message);
//                }
//            });
//        }
//        else {
//            findGuestCartByIds(currentCart.getCartIdentifier(), callback);
//        }
//    }
//
//    private void createGuestCart(ServiceCallbackOnlyOnServiceResults<String> callback) {
//        executeSimpleOnline(callback, service.createGuestCart());
//    }
//
//    private void findGuestCartByIds(@Nonnull String cartIdentifier, ServiceCallbackOnlyOnServiceResults<ShoppingCart> callback) {
//
//        ServiceCallback<ShoppingCart> finderCallback = new ServiceCallback<ShoppingCart>() {
//            @Override
//            public void onResults(ShoppingCart cart) {
//                if(cart != null && cart.getActive()) {
//                    cart.setCartIdentifier(cartIdentifier);
//
//                    getTotals(cart, new ServiceCallback<CartTotals>() {
//                        @Override
//                        public void onResults(CartTotals results) {
//                            if(results != null) {
//                                cart.setTotals(results);
//
//                                new MagentoDatabaseUtils().saveOrUpdateShoppingCart(cart);
//
//                                callback.onResults(cart);
//                                callback.onFinish();
//                            }
//                        }
//
//                        @Override
//                        public void onError(int errorCode, String message) {
//                            callback.onError(errorCode, message);
//                            callback.onFinish();
//                        }
//                    });
//                }
//                else {
//                    onError(1, "");
//                }
//            }
//
//            @Override
//            public void onError(int errorCode, String message) {
//                //CART is NOT VALID
//                new MagentoDatabaseUtils().clearCheckoutDatabase(true);
//
//                createGuestCart(new ServiceCallbackOnlyOnServiceResults<String>() {
//                    @Override
//                    public void onResults(String results) {
//                        findGuestCartByIds(results, callback);
//                    }
//
//                    @Override
//                    public void onError(int errorCode, String message) {
//                        callback.onError(errorCode, message);
//                    }
//                });
//            }
//        };
//
//        executeSimpleOnline(finderCallback, service.getGuestShoppingCartByIdentifier(cartIdentifier));
//
////        new ParallelRestService()
////                .addServiceCall(service.getGuestShoppingCartByIdentifier(cartIdentifier), finderCallback)
////                .addServiceCall(service.getGuestCartTotals(cartIdentifier), totalsCallback)
////                .setFinishService(() -> {
////
////                    ShoppingCart cart = pairCart.getFirst();
////                    cart.setTotals(pairCart.getSecond());
////
////                    new MagentoDatabaseUtils().saveOrUpdateShoppingCart(cart);
////
////                    callback.onResults(cart);
////                    callback.onFinish();
////                })
////                .execute();
//    }
//
//    private void getTotals(ShoppingCart cart, ServiceCallback<CartTotals> callback) {
//        executeSimpleOnline(callback, service.getGuestCartTotals(cart.getCartIdentifier()));
//    }
//
//    public void estimateShippingMethods(Address address, ServiceCallback<List<ShippingMethod>> callback) {
//        ShoppingCart cart = new MagentoDatabaseUtils().retrieveCart();
//        if(cart == null) return;
//
//        executeSimpleOnline(callback, service.getGuestShippingMethods(cart.getCartIdentifier(), new AddressDTO(address)));
//    }
//
//    public void getPaymentMethods(ServiceCallback<List<PaymentMethod>> callback) {
//        ShoppingCart cart = new MagentoDatabaseUtils().retrieveCart();
//        if(cart == null) return;
//
//        executeSimpleOnline(callback, service.getGuestPaymentMethods(cart.getCartIdentifier()));
//    }
//
//    public void addItemToCart(CartItem item, ServiceCallback<CartItem> callback) {
//        ShoppingCart cart = new MagentoDatabaseUtils().retrieveCart();
//        if(cart == null) {
//
//            //If does not exist, we need to create it  before
//            getGuestCart(new ServiceCallbackOnlyOnServiceResults<ShoppingCart>() {
//                @Override
//                public void onResults(ShoppingCart results) {
//                    addItemToCart(item, callback);
//                }
//            });
//
//            return;
//        }
//
//        item.setQuoteId(cart.getCartIdentifier());
//
//        ServiceCallback<CartItem> firstCallback = new ServiceCallback<CartItem>() {
//            @Override
//            public void onResults(CartItem results) {
//                if(results != null) {
//                    MagentoDatabaseUtils dbUtils = new MagentoDatabaseUtils();
//                    ShoppingCart cart = dbUtils.retrieveCart();
//
//                    cart.getItems().add(results);
//
//                    dbUtils.saveOrUpdateShoppingCart(cart);
//                }
//
//                callback.onResults(results);
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
//        executeSimpleOnline(firstCallback, service.addItemToGuestCart(cart.getCartIdentifier(), new CartItemDto(item)));
//    }

    public void checkIsEmailAvailable(String email, ServiceCallback<Boolean> callback) {
        executeSimpleOnline(callback, service.isEmailAvailable(new CustomerEmailCheckerDTO(email)));
    }

//    public void sendBillingAddress(Address address, ServiceCallback callback) {
//        ShoppingCart cart = new MagentoDatabaseUtils().retrieveCart();
//        if(cart == null) return;
//
//        ServiceCallback<String> firstCallback = new ServiceCallback<String>() {
//            @Override
//            public void onResults(String results) {
//                new MagentoDatabaseUtils().saveAddress(address);
//
//                callback.onResults(results);
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
//        executeSimpleOnline(firstCallback, service.postGuestBillingAddress(cart.getCartIdentifier(), address));
//    }
//
//    public void shippingAddressEstimation(Address address, ShippingMethod shippingMethod, ServiceCallback callback) {
//        ShoppingCart cart = new MagentoDatabaseUtils().retrieveCart();
//        if(cart == null) return;
//
//        ServiceCallback firstCallback = new ServiceCallback() {
//            @Override
//            public void onResults(Object results) {
//                new MagentoDatabaseUtils().saveAddress(address);
//
//                callback.onResults(results);
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
//        address.setSameAsBilling(1);
//
//        ShippingAddressDTO.AddressInformation addressInfo = new ShippingAddressDTO.AddressInformation();
//        addressInfo.setShippingAddress(address);
//        addressInfo.setBillingAddress(address);
//        addressInfo.setShippingCarrierCode(shippingMethod.carrierCode);
//        addressInfo.setShippingMethodCode(shippingMethod.methodCode);
//
//        executeSimpleOnline(firstCallback, service.getGuestShippingInformation(cart.getCartIdentifier(), new ShippingAddressDTO(addressInfo)));
//    }
//
//    public void getAddresses(ServiceCallback<List<Address>> callback) {
//        callback.onResults(new MagentoDatabaseUtils().getAddresses());
//        callback.onFinish();
//    }
//
//    public void changeCartItemQuantity(Long cartItemId, int quantity, ServiceCallback<CartItem> callback) {
//        ShoppingCart cart = new MagentoDatabaseUtils().retrieveCart();
//        if(cart == null) {
//
//            //If does not exist, we need to create it  before
//            getGuestCart(new ServiceCallbackOnlyOnServiceResults<ShoppingCart>() {
//                @Override
//                public void onResults(ShoppingCart results) {
//                    changeCartItemQuantity(cartItemId, quantity, callback);
//                }
//            });
//
//            return;
//        }
//        CartItem item = new CartItem();
//        item.setQuoteId(cart.getCartIdentifier());
//        item.setItemId(cartItemId);
//        item.setQty(quantity);
//
//        ServiceCallback<CartItem> firstCallback = new ServiceCallback<CartItem>() {
//            @Override
//            public void onResults(CartItem results) {
//                if(results != null) {
//                    MagentoDatabaseUtils dbUtils = new MagentoDatabaseUtils();
//                    ShoppingCart cart = dbUtils.retrieveCart();
//
//                    int index = -1;
//                    if((index = cart.getItems().indexOf(item)) > -1) {
//                        cart.getItems().remove(index);
//                        cart.getItems().add(index, item);
//                    }
//                    else {
//                        cart.getItems().add(item);
//                    }
//
//                    dbUtils.saveOrUpdateShoppingCart(cart);
//                }
//
//                callback.onResults(results);
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
//        executeSimpleOnline(firstCallback, service.updateGuestCartItem(cart.getCartIdentifier(), cartItemId, new CartItemDto(item)));
//    }
//
//    public void deleteCartItem(Long cartItemId, ServiceCallback<Boolean> callback) {
//        ShoppingCart cart = new MagentoDatabaseUtils().retrieveCart();
//        if(cart == null) return;
//
//        ServiceCallback<Boolean> firstCallback = new ServiceCallback<Boolean>() {
//            @Override
//            public void onResults(Boolean results) {
//                if(results != null && results) {
//                    MagentoDatabaseUtils dbUtils = new MagentoDatabaseUtils();
//                    ShoppingCart cart = dbUtils.retrieveCart();
//
//                    int index;
//
//                    CartItem dbCartItem = new CartItem();
//                    dbCartItem.setItemId(cartItemId);
//
//                    if((index = cart.getItems().indexOf(dbCartItem)) > -1) {
//                        cart.getItems().remove(index);
//                    }
//
//                    dbUtils.saveOrUpdateShoppingCart(cart);
//                }
//
//                callback.onResults(results);
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
//        executeSimpleOnline(firstCallback, service.deleteGuestCartItem(cart.getCartIdentifier(), cartItemId));
//    }
//
//    public void applyDiscountCoupon(String coupon, ServiceCallback<Boolean> callback) {
//        ShoppingCart cart = new MagentoDatabaseUtils().retrieveCart();
//        if(cart == null) return;
//
//        executeSimpleOnline(callback, service.applyGuestCoupon(cart.getCartIdentifier(), coupon));
//    }
//
//    public void sendDeliveryNotes(String deliveryNotes, ServiceCallback<ResponseBody> callback) {
//        ShoppingCart cart = new MagentoDatabaseUtils().retrieveCart();
//        if(cart == null) return;
//
//        DeliveryNotesDto dto = new DeliveryNotesDto();
//        dto.setCartId(cart.getCartIdentifier());
//
//        DeliveryNotesDto.OrderComment comment = new DeliveryNotesDto.OrderComment();
//        comment.setComment(deliveryNotes);
//
//        dto.setOrderComment(comment);
//
//        executeSimpleOnline(callback, service.setGuestDeliveryNotes(cart.getCartIdentifier(), dto));
//    }
//
//
//    public void placeOrder(Address billingAddress, PaymentMethod paymentMethod, String email, ServiceCallback<String> callback) {
//        placeOrder(billingAddress, paymentMethod, null, email, callback);
//    }
//    public void placeOrder(Address billingAddress, PaymentMethod paymentMethod, PaymentAssignementMethodDTO.CreditCardAdditionalData ccData, String email, ServiceCallback<String> callback) {
//        ShoppingCart cart = new MagentoDatabaseUtils().retrieveCart();
//        if(cart == null) return;
//
//        PlaceOrderDTO dto = new PlaceOrderDTO();
//        dto.setBillingAddress(billingAddress);
//
//        PaymentAssignementMethodDTO pmDto = new PaymentAssignementMethodDTO(paymentMethod.getCode());
//
//        pmDto.setAdditionalData(ccData);
//
//        dto.setPaymentMethod(pmDto);
//        dto.setEmail(email);
//
//        executeSimpleOnline(callback, service.placeOrder(cart.getCartIdentifier(), dto));
//    }

    public void getCountries(ServiceCallback<List<CountryRegion>> callback) {
        executeSimpleOnline(callback, service.getCountries());
    }

    public void executeUrl(String url, ServiceCallback<ResponseBody> callback) {
        executeSimpleOnline(callback, service.executeUrl(url));
    }



    @SuppressWarnings("unchecked")
    public class ParallelRestService {

        Map<Call, ServiceCallback> services;
        FinishHandler finishHandler;

        public ParallelRestService() {
            services = new HashMap<>();
        }

        public ParallelRestService addServiceCall(Call call, ServiceCallback callback) {
            services.put(call, callback);

            return this;
        }

        public ParallelRestService setFinishService(FinishHandler finishHandler) {
            this.finishHandler = finishHandler;

            return this;
        }

        public void execute() {
            if(finishHandler == null) throw new IllegalStateException("A FinishHandler is needed");

            FinalInteger numServices = new FinalInteger(services.size());

            for(Call call : services.keySet()) {
                ServiceCallback serviceCallback = services.get(call);

                ServiceCallback firstCallback = new ServiceCallback<Object>() {
                    @Override
                    public  void  onResults(Object results) {
                        serviceCallback.onResults(results);

                        numServices.removeValue();

                        if(numServices.getValue() <= 0) {
                            finishHandler.onFinish();
                        }
                    }

                    @Override
                    public void onError(int errorCode, String message) {
                        serviceCallback.onError(errorCode, message);
                    }

                    @Override
                    public void onFinish() {}
                };

                executeSimpleOnline(firstCallback, call);

            }
        }
    }

    /** CUSTOMER **/
    public void isLoggedCustomer(ServiceCallback<Boolean> callback) {
        callback.onResults(new MagentoDatabaseUtils().getCurrentCustomer() != null);
        callback.onFinish();
    }
    public void createCustomer(CustomerRegisterDTO customer, ServiceCallback<Customer> callback) {
        ServiceCallback<Customer> firstCallback = new ServiceCallback<Customer>() {
            @Override
            public void onResults(Customer results) {
                if(results != null) {

                    MagentoDatabaseUtils dbUtils = new MagentoDatabaseUtils();
                    dbUtils.saveCustomer(results);
                }

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

        executeSimpleOnline(firstCallback, service.registerCustomer(customer));
    }

    public void loginCustomer(String email, String pass, ServiceCallback<Customer> callback) {
        CustomerLoginDTO dto = new CustomerLoginDTO(email, pass);

        ServiceCallback<String> firstCallback = new ServiceCallback<String>() {
            @Override
            public void onResults(String results) {
                if(results != null) {
                    MagentoSettings.saveCustomerToken(currentContext, results);

                    new CustomerRestService(currentContext).getCurrentCustomerData(callback);
                }
                else {
                    callback.onFinish();
                }
            }

            @Override
            public void onError(int errorCode, String message) {
                callback.onError(errorCode, message);
                callback.onFinish();
            }
        };

        executeSimpleOnline(firstCallback, service.loginCustomer(dto));
    }
}
