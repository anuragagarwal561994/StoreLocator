package com.storelocator.altaiezior;

import android.app.Activity;
import android.os.Bundle;

public class SearchProduct extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new CategoryFragment())
                    .commit();
        }
    }
}
