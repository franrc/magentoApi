package com.dekalabs.magentorestapi.utils;

import com.dekalabs.magentorestapi.pojo.Address;
import com.dekalabs.magentorestapi.pojo.AttributeOption;
import com.dekalabs.magentorestapi.pojo.Category;
import com.dekalabs.magentorestapi.pojo.CustomAttribute;
import com.dekalabs.magentorestapi.pojo.Customer;
import com.dekalabs.magentorestapi.pojo.MediaGallery;
import com.dekalabs.magentorestapi.pojo.Product;
import com.dekalabs.magentorestapi.pojo.ProductAttributes;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.RealmModule;

/**
 * Created by fran on 25/07/2018.
 */

public class DatabaseUtils {

    private static RealmConfiguration configuration;

    public static RealmConfiguration getMagentoRealmConfig() {
        if(configuration == null) {
            configuration = new RealmConfiguration.Builder()
                    .schemaVersion(0)
                    .addModule(new DatabaseUtils.MagentoModule())
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

    @RealmModule(classes = {Address.class, AttributeOption.class, Category.class, CustomAttribute.class, Customer.class, MediaGallery.class, ProductAttributes.class, })
    public static class MagentoModule {
    }

}
