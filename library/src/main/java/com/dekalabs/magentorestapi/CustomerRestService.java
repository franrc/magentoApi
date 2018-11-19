package com.dekalabs.magentorestapi;

import android.content.Context;
import android.util.Log;

import com.dekalabs.magentorestapi.config.MagentoRestConfiguration;
import com.dekalabs.magentorestapi.config.MagentoSettings;
import com.dekalabs.magentorestapi.dto.AddressDTO;
import com.dekalabs.magentorestapi.dto.CartItemDto;
import com.dekalabs.magentorestapi.dto.CustomerLoginDTO;
import com.dekalabs.magentorestapi.dto.CustomerRegisterDTO;
import com.dekalabs.magentorestapi.dto.DeliveryNotesDto;
import com.dekalabs.magentorestapi.dto.PaymentAssignementMethodDTO;
import com.dekalabs.magentorestapi.dto.PlaceOrderDTO;
import com.dekalabs.magentorestapi.dto.ProductView;
import com.dekalabs.magentorestapi.dto.ReviewPost;
import com.dekalabs.magentorestapi.dto.ReviewResponseDTO;
import com.dekalabs.magentorestapi.dto.ShippingAddressDTO;
import com.dekalabs.magentorestapi.handler.FinishHandler;
import com.dekalabs.magentorestapi.pojo.Address;
import com.dekalabs.magentorestapi.pojo.Customer;
import com.dekalabs.magentorestapi.pojo.WishList;
import com.dekalabs.magentorestapi.pojo.cart.CartItem;
import com.dekalabs.magentorestapi.pojo.cart.CartTotals;
import com.dekalabs.magentorestapi.pojo.cart.PaymentMethod;
import com.dekalabs.magentorestapi.pojo.cart.ShippingMethod;
import com.dekalabs.magentorestapi.pojo.cart.ShoppingCart;
import com.dekalabs.magentorestapi.utils.FinalInteger;
import com.dekalabs.magentorestapi.utils.MagentoDatabaseUtils;

import java.io.IOException;
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

public class CustomerRestService extends MagentoRestService {

    private String customerToken;

    public CustomerRestService(Context context) {
        super(context);

        customerToken = MagentoSettings.getCustomerToken(currentContext);

        if(isOnDebug)
            Log.i("Magento", "CUSTOMER TOKEN: " + customerToken);
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
                                "Bearer" + " " + (customerToken != null ? customerToken : MagentoRestConfiguration.getAccessToken()))
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
    }

    public void sendReview(ReviewPost reviewPost, ServiceCallback<ReviewResponseDTO> callback) {
        executeSimpleOnline(callback, customerToken != null ? service.sendCustomerReview(reviewPost) : service.sendGuestReview(reviewPost));
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

    /**** CHECKOUT *****/
    /**** GUEST *****/

    public void getCart(ServiceCallbackOnlyOnServiceResults<ShoppingCart> callback) {
        ShoppingCart currentCart = new MagentoDatabaseUtils().retrieveCart();

        if(currentCart == null) {
            createCart(new ServiceCallbackOnlyOnServiceResults<String>() {
                @Override
                public void onResults(String results) {
                    findCartByIds(results, callback);
                }

                @Override
                public void onError(int errorCode, String message) {
                    callback.onError(errorCode, message);
                }
            });
        }
        else {
            findCartByIds(currentCart.getCartIdentifier(), callback);
        }
    }

    private void createCart(ServiceCallbackOnlyOnServiceResults<String> callback) {
        executeSimpleOnline(callback, customerToken != null ? service.createCustomerCart() : service.createGuestCart());
    }

    private void findCartByIds(@Nonnull String cartIdentifier, ServiceCallbackOnlyOnServiceResults<ShoppingCart> callback) {

        ServiceCallback<ShoppingCart> finderCallback = new ServiceCallback<ShoppingCart>() {
            @Override
            public void onResults(ShoppingCart cart) {
                if(cart != null && cart.getActive()) {
                    cart.setCartIdentifier(cartIdentifier);

                    getTotals(cart, new ServiceCallback<CartTotals>() {
                        @Override
                        public void onResults(CartTotals results) {
                            if(results != null) {
                                cart.setTotals(results);

                                new MagentoDatabaseUtils().saveOrUpdateShoppingCart(cart);

                                callback.onResults(cart);
                                callback.onFinish();
                            }
                        }

                        @Override
                        public void onError(int errorCode, String message) {
                            callback.onError(errorCode, message);
                            callback.onFinish();
                        }
                    });
                }
                else {
                    onError(1, "");
                }
            }

            @Override
            public void onError(int errorCode, String message) {
                //CART is NOT VALID
                new MagentoDatabaseUtils().clearCheckoutDatabase(true);

                createCart(new ServiceCallbackOnlyOnServiceResults<String>() {
                    @Override
                    public void onResults(String results) {
                        findCartByIds(results, callback);
                    }

                    @Override
                    public void onError(int errorCode, String message) {
                        callback.onError(errorCode, message);
                    }
                });
            }
        };

        executeSimpleOnline(finderCallback, customerToken != null ? service.getCustomerShoppingCart() : service.getGuestShoppingCartByIdentifier(cartIdentifier));
    }

    private void getTotals(ShoppingCart cart, ServiceCallback<CartTotals> callback) {
        executeSimpleOnline(callback, customerToken != null ? service.getCustomerCartTotals() : service.getGuestCartTotals(cart.getCartIdentifier()));
    }

    public void estimateShippingMethods(Address address, ServiceCallback<List<ShippingMethod>> callback) {
        ShoppingCart cart = new MagentoDatabaseUtils().retrieveCart();
        if(cart == null) return;

        executeSimpleOnline(callback, customerToken != null ? service.getCustomerShippingMethods(new AddressDTO(address)) : service.getGuestShippingMethods(cart.getCartIdentifier(), new AddressDTO(address)));
    }

    public void getPaymentMethods(ServiceCallback<List<PaymentMethod>> callback) {
        ShoppingCart cart = new MagentoDatabaseUtils().retrieveCart();
        if(cart == null) return;

        executeSimpleOnline(callback, customerToken != null ? service.getCustomerPaymentMethods() : service.getGuestPaymentMethods(cart.getCartIdentifier()));
    }

    public void addItemToCart(CartItem item, ServiceCallback<CartItem> callback) {
        ShoppingCart cart = new MagentoDatabaseUtils().retrieveCart();
        if(cart == null) {

            //If does not exist, we need to create it  before
            getCart(new ServiceCallbackOnlyOnServiceResults<ShoppingCart>() {
                @Override
                public void onResults(ShoppingCart results) {
                    addItemToCart(item, callback);
                }
            });

            return;
        }

        item.setQuoteId(cart.getCartIdentifier());

        ServiceCallback<CartItem> firstCallback = new ServiceCallback<CartItem>() {
            @Override
            public void onResults(CartItem results) {
                if(results != null) {
                    MagentoDatabaseUtils dbUtils = new MagentoDatabaseUtils();
                    ShoppingCart cart = dbUtils.retrieveCart();

                    cart.getItems().add(results);

                    dbUtils.saveOrUpdateShoppingCart(cart);
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

        executeSimpleOnline(firstCallback, customerToken != null ? service.addItemToCustomerCart(new CartItemDto(item)) : service.addItemToGuestCart(cart.getCartIdentifier(), new CartItemDto(item)));
    }

    public void sendBillingAddress(Address address, ServiceCallback callback) {
        ShoppingCart cart = new MagentoDatabaseUtils().retrieveCart();
        if(cart == null) return;

        ServiceCallback<String> firstCallback = new ServiceCallback<String>() {
            @Override
            public void onResults(String results) {
                new MagentoDatabaseUtils().saveAddress(address);

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

        executeSimpleOnline(firstCallback, customerToken != null ? service.postCustomerBillingAddress(address) : service.postGuestBillingAddress(cart.getCartIdentifier(), address));
    }

    public void shippingAddressEstimation(Address address, ShippingMethod shippingMethod, ServiceCallback callback) {
        ShoppingCart cart = new MagentoDatabaseUtils().retrieveCart();
        if(cart == null) return;

        ServiceCallback firstCallback = new ServiceCallback() {
            @Override
            public void onResults(Object results) {
                new MagentoDatabaseUtils().saveAddress(address);

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

        address.setSameAsBilling(1);

        ShippingAddressDTO.AddressInformation addressInfo = new ShippingAddressDTO.AddressInformation();
        addressInfo.setShippingAddress(address);
        addressInfo.setBillingAddress(address);
        addressInfo.setShippingCarrierCode(shippingMethod.carrierCode);
        addressInfo.setShippingMethodCode(shippingMethod.methodCode);

        executeSimpleOnline(firstCallback, customerToken != null ? service.getCustomerShippingInformation(new ShippingAddressDTO(addressInfo)) : service.getGuestShippingInformation(cart.getCartIdentifier(), new ShippingAddressDTO(addressInfo)));
    }

    public void getAddresses(ServiceCallback<List<Address>> callback) {
        callback.onResults(new MagentoDatabaseUtils().getAddresses());
        callback.onFinish();
    }

    public void changeCartItemQuantity(Long cartItemId, int quantity, ServiceCallback<CartItem> callback) {
        ShoppingCart cart = new MagentoDatabaseUtils().retrieveCart();
        if(cart == null) {

            //If does not exist, we need to create it  before
            getCart(new ServiceCallbackOnlyOnServiceResults<ShoppingCart>() {
                @Override
                public void onResults(ShoppingCart results) {
                    changeCartItemQuantity(cartItemId, quantity, callback);
                }
            });

            return;
        }
        CartItem item = new CartItem();
        item.setQuoteId(cart.getCartIdentifier());
        item.setItemId(cartItemId);
        item.setQty(quantity);

        ServiceCallback<CartItem> firstCallback = new ServiceCallback<CartItem>() {
            @Override
            public void onResults(CartItem results) {
                if(results != null) {
                    MagentoDatabaseUtils dbUtils = new MagentoDatabaseUtils();
                    ShoppingCart cart = dbUtils.retrieveCart();

                    int index = -1;
                    if((index = cart.getItems().indexOf(item)) > -1) {
                        cart.getItems().remove(index);
                        cart.getItems().add(index, item);
                    }
                    else {
                        cart.getItems().add(item);
                    }

                    dbUtils.saveOrUpdateShoppingCart(cart);
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

        executeSimpleOnline(firstCallback, customerToken != null ? service.updateCustomerCartItem(cartItemId, new CartItemDto(item)) : service.updateGuestCartItem(cart.getCartIdentifier(), cartItemId, new CartItemDto(item)));
    }

    public void deleteCartItem(Long cartItemId, ServiceCallback<Boolean> callback) {
        ShoppingCart cart = new MagentoDatabaseUtils().retrieveCart();
        if(cart == null) return;

        ServiceCallback<Boolean> firstCallback = new ServiceCallback<Boolean>() {
            @Override
            public void onResults(Boolean results) {
                if(results != null && results) {
                    MagentoDatabaseUtils dbUtils = new MagentoDatabaseUtils();
                    ShoppingCart cart = dbUtils.retrieveCart();

                    int index;

                    CartItem dbCartItem = new CartItem();
                    dbCartItem.setItemId(cartItemId);

                    if((index = cart.getItems().indexOf(dbCartItem)) > -1) {
                        cart.getItems().remove(index);
                    }

                    dbUtils.saveOrUpdateShoppingCart(cart);
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

        executeSimpleOnline(firstCallback, customerToken != null ? service.deleteCustomerCartItem(cartItemId) : service.deleteGuestCartItem(cart.getCartIdentifier(), cartItemId));
    }

    public void applyDiscountCoupon(String coupon, ServiceCallback<Boolean> callback) {
        ShoppingCart cart = new MagentoDatabaseUtils().retrieveCart();
        if(cart == null) return;

        executeSimpleOnline(callback, customerToken != null ? service.applyCustomerCoupon(coupon) : service.applyGuestCoupon(cart.getCartIdentifier(), coupon));
    }

    public void sendDeliveryNotes(String deliveryNotes, ServiceCallback<ResponseBody> callback) {
        ShoppingCart cart = new MagentoDatabaseUtils().retrieveCart();
        if(cart == null) return;

        DeliveryNotesDto dto = new DeliveryNotesDto();
        dto.setCartId(cart.getCartIdentifier());

        DeliveryNotesDto.OrderComment comment = new DeliveryNotesDto.OrderComment();
        comment.setComment(deliveryNotes);

        dto.setOrderComment(comment);

        executeSimpleOnline(callback, customerToken != null ? service.setCustomerDeliveryNotes(dto) : service.setGuestDeliveryNotes(cart.getCartIdentifier(), dto));
    }


    public void placeOrder(Address billingAddress, PaymentMethod paymentMethod, String email, ServiceCallback<String> callback) {
        placeOrder(billingAddress, paymentMethod, null, email, callback);
    }

    public void placeOrder(Address billingAddress, PaymentMethod paymentMethod, PaymentAssignementMethodDTO.CreditCardAdditionalData ccData, String email, ServiceCallback<String> callback) {
        ShoppingCart cart = new MagentoDatabaseUtils().retrieveCart();
        if(cart == null) return;

        PlaceOrderDTO dto = new PlaceOrderDTO();
        dto.setBillingAddress(billingAddress);

        PaymentAssignementMethodDTO pmDto = new PaymentAssignementMethodDTO(paymentMethod.getCode());

        pmDto.setAdditionalData(ccData);

        dto.setPaymentMethod(pmDto);
        dto.setEmail(email);

        executeSimpleOnline(callback, customerToken != null ? service.placeCustomerOrder(dto) : service.placeGuestOrder(cart.getCartIdentifier(), dto));
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

    public void getCurrentCustomerData(ServiceCallback<Customer> callback) {
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

        executeSimpleOnline(firstCallback, service.getCurrentCustomerData());
    }

    public void getShippingAddresses(ServiceCallback<List<Address>> callback) {
        if(customerToken == null) {
            callback.onResults(new MagentoDatabaseUtils().getAddresses());
            callback.onFinish();
            return;
        }

        ServiceCallback<List<Address>> firstCallback = new ServiceCallback<List<Address>>() {
            @Override
            public void onResults(List<Address> results) {
                new MagentoDatabaseUtils().saveAddresses(results);

                callback.onResults(results);
            }

            @Override
            public void onError(int errorCode, String message) {
                callback.onError(errorCode, message);
                callback.onFinish();
            }
        };

        executeSimpleOnline(firstCallback, service.getCustomerShippingAddresses());
    }

    public void getBillingAddresses(ServiceCallback<List<Address>> callback) {
        if(customerToken == null) {
            callback.onResults(null);
            callback.onFinish();
            return;
        }

        ServiceCallback<List<Address>> firstCallback = new ServiceCallback<List<Address>>() {
            @Override
            public void onResults(List<Address> results) {
                new MagentoDatabaseUtils().saveAddresses(results);

                callback.onResults(results);
            }

            @Override
            public void onError(int errorCode, String message) {
                callback.onError(errorCode, message);
                callback.onFinish();
            }
        };

        executeSimpleOnline(firstCallback, service.getCustomerShippingAddresses());
    }
}
