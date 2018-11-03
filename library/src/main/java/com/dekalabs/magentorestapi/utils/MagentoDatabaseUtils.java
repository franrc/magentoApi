package com.dekalabs.magentorestapi.utils;

import com.dekalabs.magentorestapi.dto.ProductView;
import com.dekalabs.magentorestapi.pojo.Address;
import com.dekalabs.magentorestapi.pojo.AttributeOption;
import com.dekalabs.magentorestapi.pojo.Category;
import com.dekalabs.magentorestapi.pojo.CustomAttribute;
import com.dekalabs.magentorestapi.pojo.Product;
import com.dekalabs.magentorestapi.pojo.WishList;
import com.dekalabs.magentorestapi.pojo.cart.ShoppingCart;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.RealmModule;
import java8.util.stream.StreamSupport;

/**
 * Created by fran on 25/07/2018.
 */

public class MagentoDatabaseUtils {

    private static RealmConfiguration configuration;

    public static RealmConfiguration getMagentoRealmConfig() {
        if(configuration == null) {
            configuration = new RealmConfiguration.Builder()
                    .schemaVersion(0)
                    .addModule(new MagentoDatabaseUtils.MagentoModule())
                    .name("magento.realm")
                    .deleteRealmIfMigrationNeeded()
                    .build();
        }

        return configuration;
    }

    private Realm getRealmInstance() {
        return Realm.getInstance(getMagentoRealmConfig());
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

    public void addProductToWishList(String productSku) {
        if(productSku == null) return;

        Realm realm = getRealmInstance();
        realm.beginTransaction();

        WishList wishList = realm.where(WishList.class).findFirst();

        if(wishList == null) {
            wishList = new WishList();
            wishList.setId(1L);
        }

        wishList.getProducts().add(productSku);

        realm.copyToRealmOrUpdate(wishList);

        realm.commitTransaction();
        realm.close();
    }

    public boolean removeProductFromWishList(String productSku) {
        if(productSku == null) return false;

        Realm realm = getRealmInstance();
        realm.beginTransaction();

        WishList wishList = realm.where(WishList.class).findFirst();

        boolean removed = false;

        if(wishList != null) {
            removed = wishList.getProducts().remove(productSku);
            realm.copyToRealmOrUpdate(wishList);
        }

        realm.commitTransaction();
        realm.close();

        return removed;
    }

    public WishList getWishList() {
        Realm realm = getRealmInstance();
        WishList wishList = realm.where(WishList.class).findFirst();

        if(wishList != null) {
            wishList = realm.copyFromRealm(wishList);
        }

        realm.close();
        return wishList;
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
                    pw.getMainProduct().setFavourite(unmanagedWishList.getProducts().contains(pw.getMainProduct().getSku()));

                    if(pw.getChildren() != null && pw.getChildren().size() > 0) {
                        StreamSupport.stream(pw.getChildren())
                                .parallel()
                                .forEach(p -> {
                                    p.setFavourite(unmanagedWishList.getProducts().contains(p.getSku()));
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
                .forEach(p -> p.setFavourite(unmanagedWishList.getProducts().contains(p.getSku())));

        realm.close();
    }

    /** CART ***/
    public ShoppingCart retrieveCart(Long idCart) {
        if(idCart == null) return null;

        Realm realm = getRealmInstance();
        ShoppingCart cart = realm.where(ShoppingCart.class).equalTo("id", idCart).findFirst();

        if(cart == null) {
            realm.close();
            return null;
        }

        return copyFromRealm(realm, cart, true);
    }

    public void saveOrUpdateShoppingCart(ShoppingCart cart) {
        if(cart == null) return;

        Realm realm = getRealmInstance();
        realm.beginTransaction();

        //If exists on database, we only need to update it
        if(realm.where(ShoppingCart.class).equalTo("id", cart.getId()).findFirst() != null) {
            realm.copyToRealmOrUpdate(cart);
            realm.commitTransaction();
            realm.close();

            return;
        }

        //Otherwise, we must delete all possible pre created carts before inserting the new one
        RealmResults<ShoppingCart> oldCarts = realm.where(ShoppingCart.class).findAll();
        oldCarts.deleteAllFromRealm();

        realm.copyToRealm(cart);
        realm.commitTransaction();
        realm.close();
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

    private <DATA extends RealmObject> DATA copyFromRealm(Realm realm, DATA result, boolean closeRealm) {
        DATA copiedData = null;

        if(result != null)
            copiedData = realm.copyFromRealm(result);

        if(closeRealm)
            realm.close();

        return copiedData;
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

}