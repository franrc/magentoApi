package com.dekalabs;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.dekalabs.magentorestapi.DKRestService;
import com.dekalabs.magentorestapi.MagentoRestService;
import com.dekalabs.magentorestapi.R;
import com.dekalabs.magentorestapi.ServiceCallbackOnlyOnServiceResults;
import com.dekalabs.magentorestapi.config.MagentoRestConfiguration;
import com.dekalabs.magentorestapi.dto.MagentoListResponse;
import com.dekalabs.magentorestapi.dto.MagentoResponse;
import com.dekalabs.magentorestapi.pojo.Category;
import com.dekalabs.magentorestapi.pojo.Customer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DKRestService.setIsOnDebug(true);
        new MagentoRestConfiguration.Builder().setAccessToken("09reus7bata2o4f146jfa25www2dxouc").setAppUrl("http://dev-magentoapp.vaimo.com/rest/V1/").build();


//        new MagentoRestService(this).getCategoryList(null, 3, new ServiceCallbackOnlyOnServiceResults<MagentoResponse<Category>>() {
//            @Override
//            public void onResults(MagentoResponse<Category> results) {
//                Log.i("Magento", results != null && results.getData() != null ? results.getData().getName() : "nulo");
//            }
//        });

        new MagentoRestService(this).getMe(new ServiceCallbackOnlyOnServiceResults<MagentoResponse<Customer>>() {
            @Override
            public void onResults(MagentoResponse<Customer> results) {
                if(results == null) Log.i("Magento", "null");
                else {
                    if (results.getError() == null)
                        Log.i("Magento", results.getData() != null ? results.getData().getFirstname() : "nulo");
                    else {
                        Log.i("Magento", "Error: " + results.getError().getError());
                    }
                }
            }
        });
    }

}
