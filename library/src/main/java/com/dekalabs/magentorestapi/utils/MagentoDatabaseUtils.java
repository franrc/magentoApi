package com.dekalabs.magentorestapi.utils;

import com.dekalabs.magentorestapi.dto.ProductView;
import com.dekalabs.magentorestapi.pojo.Address;
import com.dekalabs.magentorestapi.pojo.AttributeOption;
import com.dekalabs.magentorestapi.pojo.Category;
import com.dekalabs.magentorestapi.pojo.CustomAttribute;
import com.dekalabs.magentorestapi.pojo.Customer;
import com.dekalabs.magentorestapi.pojo.Product;
import com.dekalabs.magentorestapi.pojo.WishList;
import com.dekalabs.magentorestapi.pojo.WishListItem;
import com.dekalabs.magentorestapi.pojo.cart.CartItem;
import com.dekalabs.magentorestapi.pojo.cart.CartTotals;
import com.dekalabs.magentorestapi.pojo.cart.Currency;
import com.dekalabs.magentorestapi.pojo.cart.ShoppingCart;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.RealmModule;
import java8.util.stream.StreamSupport;

/**
 * Created by fran on 25/07/2018.
 */

public class MagentoDatabaseUtils {

    private static RealmConfiguration getMagentoRealmConfig() {
        return new RealmConfiguration.Builder()
                    .schemaVersion(0)
                    .addModule(new MagentoDatabaseUtils.MagentoModule())
                    .name("magento.realm")
                    .deleteRealmIfMigrationNeeded()
                    .build();
    }

    private static RealmConfiguration getCheckoutRealmConfig() {
        return new RealmConfiguration.Builder()
                .schemaVersion(0)
                .addModule(new MagentoDatabaseUtils.CheckoutModule())
                .name("magento.checkout.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
    }


    private Realm getRealmInstance() {
        return Realm.getInstance(getMagentoRealmConfig());
    }

    private Realm getCheckoutRealmInstance() {
        return Realm.getInstance(getCheckoutRealmConfig());
    }

    public List<CustomAttribute> getCustomAttributes() {
        Realm realm = getRealmInstance();

        RealmResults<CustomAttribute> attributes = realm.where(CustomAttribute.class).findAll();

        return copyFromRealm(realm, attributes, true);
    }

    public List<Category> getCategoriesByParent(Long parentId) {
        Realm realm = getRealmInstance();

        RealmResults<Category> categories = realm.where(Category.class).equalTo("parentId", parentId).findAll();

        return copyFromRealm(realm, categories, true);
    }

    public void saveCategories(Long parentCategory, List<Category> categories) {
        Realm realm = getRealmInstance();
        realm.beginTransaction();

        RealmResults<Category> oldCategories = realm.where(Category.class).equalTo("parentId", parentCategory).findAll();
        oldCategories.deleteAllFromRealm();

        if(categories == null) return;

        realm.copyToRealmOrUpdate(categories);
        realm.commitTransaction();
        realm.close();
    }


    public void saveCustomAttributes(List<CustomAttribute> customAttributes) {
        Realm realm = getRealmInstance();
        realm.beginTransaction();

        realm.delete(CustomAttribute.class);
        realm.delete(AttributeOption.class);

        if(customAttributes == null) return;

        Long id = 1L;

        for(CustomAttribute attr : customAttributes) {

            for(AttributeOption option: attr.getOptions()) {
                option.setId(id++);
                option.setAttributeCode(attr.getAttributeCode());
            }
        }

        realm.copyToRealmOrUpdate(customAttributes);
        realm.commitTransaction();
        realm.close();
    }

    public void saveProducts(Long categoryId, List<Product> products) {
        Realm realm = getRealmInstance();
        realm.beginTransaction();

        RealmResults<Product> oldProductsByCat = realm.where(Product.class).equalTo("categoryId", categoryId).findAll();

        if(oldProductsByCat != null)
            oldProductsByCat.deleteAllFromRealm();

        if(products == null) return;

        for(Product product : products) {
            product.setCategoryId(categoryId);

//            if(product.getCustomAttributes() == null) continue;
//
//            for(ProductAttributes attr : product.getCustomAttributes()) {
//
//                CustomAttribute customAttribute = realm.where(CustomAttribute.class).equalTo("attributeCode", attr.getAttributeCode()).findFirst();
//
//                if(customAttribute != null) {
//                    attr.setAttribute(customAttribute);
//                }
//
//                attr.generatePrimaryKey(product.getId());
//            }
        }

        realm.copyToRealmOrUpdate(products);

        realm.commitTransaction();
        realm.close();
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        if(categoryId == null) return null;

        Realm realm = getRealmInstance();

        RealmResults<Product> products = realm.where(Product.class).equalTo("categoryId", categoryId).findAll();

        return copyFromRealm(realm, products, true);
    }

    private <DATA extends RealmObject> List<DATA> copyFromRealm(Realm realm, RealmResults<DATA> results, boolean closeRealm) {
        List<DATA> data = null;

        if(results != null)
            data = realm.copyFromRealm(results);

        if(closeRealm)
            realm.close();

        return data;
    }

    public AttributeOption findAttributeByValue(String code, String value) {
        if(value == null) return null;

        Realm realm = getRealmInstance();

        AttributeOption attr = realm.where(AttributeOption.class)
                                                            .equalTo("attributeCode", code)
                                                            .equalTo("value", value)
                                                            .findFirst();
        return copyFromRealm(realm, attr, true);
    }

    public void createWishList(List<WishListItem> items) {
        if(items == null) return;

        Realm realm = getRealmInstance();
        realm.beginTransaction();

        WishList wishList = realm.where(WishList.class).findFirst();

        if(wishList != null) wishList.deleteFromRealm();

        wishList = new WishList();
        RealmList<WishListItem> list = new RealmList<>();
        list.addAll(items);

        wishList.setProducts(list);

        realm.copyToRealmOrUpdate(wishList);

        realm.commitTransaction();
        realm.close();
    }

//    public void addProductToWishList(WishListItem item) {
//        if(item == null) return;
//
//        Realm realm = getRealmInstance();
//        realm.beginTransaction();
//
//        WishList wishList = realm.where(WishList.class).findFirst();
//
//        if(wishList == null) {
//            wishList = new WishList();
//            wishList.setId(1L);
//        }
//
//        wishList.getProducts().add(item);
//
//        realm.copyToRealmOrUpdate(wishList);
//
//        realm.commitTransaction();
//        realm.close();
//    }

//    public boolean removeProductFromWishList(WishListItem item) {
//        if(item == null) return false;
//
//        Realm realm = getRealmInstance();
//        realm.beginTransaction();
//
//        WishList wishList = realm.where(WishList.class).findFirst();
//
//        boolean removed = false;
//
//        if(wishList != null) {
//            removed = wishList.getProducts().remove(item);
//            realm.copyToRealmOrUpdate(wishList);
//        }
//
//        realm.commitTransaction();
//        realm.close();
//
//        return removed;
//    }

    public WishList getWishList() {
        Realm realm = getRealmInstance();
        WishList wishList = realm.where(WishList.class).findFirst();

        if(wishList != null) {
            wishList = realm.copyFromRealm(wishList);
        }

        realm.close();
        return wishList;
    }

    public Long getWishlistItemIdByProduct(Long productId) {
        if(productId == null) return null;

        Realm realm = getRealmInstance();
        WishListItem item = realm.where(WishListItem.class).equalTo("productId", productId).findFirst();

        if(item == null) return null;

        return item.getId();

    }

    public void markProductViewAsFavourite(ProductView productView) {
        if(productView == null) return;

        markProductsAsFavourites(Arrays.asList(productView));
    }

    public void markProductsAsFavourites(List<ProductView> productViews) {
        if(productViews == null || productViews.size() == 0) return;

        Realm realm = getRealmInstance();
        WishList wishList = realm.where(WishList.class).findFirst();

        if(wishList == null) {
            realm.close();
            return;
        }

        final WishList unmanagedWishList = realm.copyFromRealm(wishList);

        StreamSupport.stream(productViews)
                .parallel()
                .forEach(pw -> {
                    pw.getMainProduct().setFavourite(unmanagedWishList.hasProduct(pw.getMainProduct().getId()));

                    if(pw.getChildren() != null && pw.getChildren().size() > 0) {
                        StreamSupport.stream(pw.getChildren())
                                .parallel()
                                .forEach(p -> {
                                    p.setFavourite(unmanagedWishList.hasProduct(p.getId()));
                                });
                    }
                });

        realm.close();
    }

    public void checkProductFavourite(Product product) {
        if(product == null) return;

        checkProductFavourite(Arrays.asList(product));
    }

    public void checkProductFavourite(List<Product> products) {
        if(products == null || products.size() == 0) return;

        Realm realm = getRealmInstance();
        WishList wishList = realm.where(WishList.class).findFirst();

        if(wishList == null) {
            realm.close();
            return;
        }

        final WishList unmanagedWishList = realm.copyFromRealm(wishList);

        StreamSupport.stream(products)
                .parallel()
                .forEach(p -> p.setFavourite(unmanagedWishList.hasProduct(p.getId())));

        realm.close();
    }

    /** CART ***/
    public ShoppingCart retrieveCart() {
        Realm realm = getCheckoutRealmInstance();
        ShoppingCart cart = realm.where(ShoppingCart.class).findFirst();

        if(cart == null) {
            realm.close();
            return null;
        }

        return copyFromRealm(realm, cart, true);
    }

    public void saveOrUpdateShoppingCart(ShoppingCart cart) {
        if(cart == null) return;

        Realm realm = getCheckoutRealmInstance();
        realm.beginTransaction();

        //Otherwise, we must delete all possible pre created carts before inserting the new one
        RealmResults<CartItem> oldCartItems = realm.where(CartItem.class).findAll();
        if(oldCartItems != null) oldCartItems.deleteAllFromRealm();

        RealmResults<ShoppingCart> oldCarts = realm.where(ShoppingCart.class).findAll();
        if(oldCarts != null) oldCarts.deleteAllFromRealm();

        realm.copyToRealmOrUpdate(cart);
        realm.commitTransaction();
        realm.close();
    }

    public void saveAddresses(List<Address> addresses) {
        for(Address address : addresses) {
            saveAddress(address);
        }
    }

    public void saveAddress(Address address) {
        if(address == null) return;

        Realm realm = getRealmInstance();
        realm.beginTransaction();

        realm.copyToRealmOrUpdate(address);

        realm.commitTransaction();
        realm.close();
    }

    public List<Address> getAddresses() {
        Realm realm = getRealmInstance();

        List<Address> addresses = realm.where(Address.class).findAll();

        if(addresses != null)
            addresses = realm.copyFromRealm(addresses);

        realm.close();
        return addresses;
    }

    public void deleteAddesses() {
        Realm realm = getRealmInstance();
        realm.beginTransaction();

        RealmResults<Address> addresses = realm.where(Address.class).findAll();

        if(addresses != null)
            addresses.deleteAllFromRealm();

        realm.commitTransaction();
        realm.close();
    }

    public void saveCustomer(Customer customer) {
        Realm realm = getRealmInstance();
        realm.beginTransaction();

        RealmResults<Customer> customers = realm.where(Customer.class).findAll();

        if(customers != null)
            customers.deleteAllFromRealm();

        realm.copyToRealm(customer);

        realm.commitTransaction();
        realm.close();
    }

    public void deleteCustomer(Long customerID) {
        Realm realm = getRealmInstance();
        realm.beginTransaction();

        Customer customer = realm.where(Customer.class).equalTo("id", customerID).findFirst();

        if(customer != null)
            customer.deleteFromRealm();

        realm.commitTransaction();
        realm.close();
    }

    public Customer getCustomer(Long customerId) {
        Realm realm = getRealmInstance();

        Customer customer = realm.where(Customer.class).equalTo("id", customerId).findFirst();

        if(customer != null)
            customer = realm.copyFromRealm(customer);

        realm.close();
        return customer;
    }

    public Customer getCurrentCustomer() {
        Realm realm = getRealmInstance();

        Customer customer = realm.where(Customer.class).findFirst();

        if(customer != null)
            customer = realm.copyFromRealm(customer);

        realm.close();
        return customer;
    }

    private <DATA extends RealmObject> DATA copyFromRealm(Realm realm, DATA result, boolean closeRealm) {
        DATA copiedData = null;

        if(result != null)
            copiedData = realm.copyFromRealm(result);

        if(closeRealm)
            realm.close();

        return copiedData;
    }

    public void clearCustomer() {
        Customer c = getCurrentCustomer();

        if(c != null)
            deleteCustomer(c.getId());

        clearCheckoutDatabase(true);
    }

    public void clearCheckoutDatabase(boolean deleteAlsoAddresses) {
        Realm realm = getCheckoutRealmInstance();

        try {
            realm.close();
            Realm.deleteRealm(realm.getConfiguration());
            //Realm file has been deleted.
        } catch (Exception ex){
            ex.printStackTrace();
            //No Realm file to remove.
        }

        if(deleteAlsoAddresses) {
            deleteAddesses();
        }
    }

    public void clearDatabase() {
        Realm realm = getRealmInstance();

        try {
            realm.close();
            Realm.deleteRealm(realm.getConfiguration());
            //Realm file has been deleted.
        } catch (Exception ex){
            ex.printStackTrace();
            //No Realm file to remove.
        }
    }

    @RealmModule(library = true, allClasses = true)
    public static class MagentoModule {
    }

    @RealmModule(library = true, allClasses = false, classes = {ShoppingCart.class, CartItem.class, CartTotals.class, Currency.class, Address.class})
    public static class CheckoutModule {
    }

}
