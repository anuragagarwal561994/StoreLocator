package com.storelocator.altaiezior;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.common.internal.m;
import com.storelocator.altaiezior.api.ProductSearchServer;
import com.storelocator.altaiezior.api.UserLoginServer;

import retrofit.RestAdapter;

/**
 * Created by altaiezior on 20/4/15.
 */
public class SearchTask extends AsyncTask<Void, Void, ProductSearchServer.ProductList> {

    private final SearchResultFragment mSearchResultFragment;
    private final String productName;
    private final Long parent_id;


    public SearchTask(Long parent_id, String searchQuery, SearchResultFragment context){
        this.parent_id =parent_id;
        mSearchResultFragment = context;
        productName = searchQuery;
    }

    @Override
    protected ProductSearchServer.ProductList doInBackground(Void... voids) {
        ProductSearchServer productSearchServer = new RestAdapter.Builder()
                .setServer(mSearchResultFragment.getString(R.string.base_url))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(ProductSearchServer.class);
        return productSearchServer.search_product(productName, parent_id);
    }

    @Override
    protected void onPostExecute(ProductSearchServer.ProductList productList) {
        mSearchResultFragment.showProgress(false);
        if(productList!=null){
            if(productList.getProducts()!=null)
                mSearchResultFragment.setmAdapter(new ProductArrayAdapter(
                    mSearchResultFragment.getActivity(), productList.getProducts()));
            else
                Toast.makeText(mSearchResultFragment.getActivity(), "No item in the list",
                        Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(mSearchResultFragment.getActivity(), "Problem getting the search result",
                    Toast.LENGTH_SHORT).show();
        }
        super.onPostExecute(productList);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mSearchResultFragment.showProgress(false);
    }
}
