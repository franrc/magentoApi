package com.dekalabs.magentorestapi.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dekalabs.magentorestapi.pojo.AttributeOption;
import com.dekalabs.magentorestapi.pojo.Category;
import com.dekalabs.magentorestapi.pojo.CustomAttribute;
import com.dekalabs.magentorestapi.pojo.Product;
import com.dekalabs.magentorestapi.pojo.ProductAttributes;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import java8.util.stream.StreamSupport;

/**
 * Created by fran on 25/07/2018.
 */

public class DatabaseUtils {

    private static DatabaseUtils instance = null;
    private static Realm realm;

    protected DatabaseUtils() {
        // Exists only to defeat instantiation.
    }
    public static DatabaseUtils getInstance() {
        if(instance == null) {
            instance = new DatabaseUtils();
        }

        if(realm == null || realm.isClosed())
            realm = Realm.getDefaultInstance();

        return instance;
    }

    public List<CustomAttribute> getCustomAttributes() {
        RealmResults<CustomAttribute> attributes = realm.where(CustomAttribute.class).findAll();

        if(attributes != null)
            return realm.copyFromRealm(attributes);

        return null;
    }

    public List<Category> getCategoriesByParent(Long parentId) {
        RealmResults<Category> attributes = realm.where(Category.class).equalTo("parentId", parentId).findAll();

        if(attributes != null)
            return realm.copyFromRealm(attributes);

        return null;
    }

    public void saveCategories(Long parentCategory, List<Category> categories) {
        realm.beginTransaction();

        RealmResults<Category> oldCategories = realm.where(Category.class).equalTo("parentId", parentCategory).findAll();
        oldCategories.deleteAllFromRealm();

        if(categories == null) return;

        realm.copyToRealmOrUpdate(categories);
        realm.commitTransaction();
    }


    public void saveCustomAttributes(List<CustomAttribute> customAttributes) {
        realm.beginTransaction();

        realm.delete(CustomAttribute.class);
        realm.delete(AttributeOption.class);

        if(customAttributes == null) return;

        Long id = 1L;

        for(CustomAttribute attr : customAttributes) {

            for(AttributeOption option: attr.getOptions()) {
                option.setId(id++);
            }
        }

        realm.copyToRealmOrUpdate(customAttributes);
        realm.commitTransaction();
    }

    public void saveProducts(Long categoryId, List<Product> products) {
        realm.beginTransaction();

        RealmResults<Product> oldProductsByCat = realm.where(Product.class).equalTo("categoryId", categoryId).findAll();

        if(oldProductsByCat != null)
            oldProductsByCat.deleteAllFromRealm();

        if(products == null) return;

        for(Product product : products) {
            product.setCategoryId(categoryId);

            if(product.getCustomAttributes() == null) continue;

            for(ProductAttributes attr : product.getCustomAttributes()) {

                CustomAttribute customAttribute = realm.where(CustomAttribute.class).equalTo("attributeCode", attr.getAttribute().getAttributeCode()).findFirst();

                if(customAttribute != null) {
                    attr.setAttribute(customAttribute);
                }

                attr.generatePrimaryKey(product.getId());
            }
        }

        realm.copyToRealmOrUpdate(products);

        realm.commitTransaction();
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        if(categoryId == null) return null;

        RealmResults<Product> products = realm.where(Product.class).equalTo("categoryId", categoryId).findAll();

        if(products != null)
            return realm.copyFromRealm(products);

        return null;
    }

    public void clearDatabase() {
        try {
            realm.close();
            Realm.deleteRealm(realm.getConfiguration());
            //Realm file has been deleted.
        } catch (Exception ex){
            ex.printStackTrace();
            //No Realm file to remove.
        }
    }

}
