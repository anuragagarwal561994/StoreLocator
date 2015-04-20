package com.storelocator.altaiezior;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class SearchResult extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle b = new Bundle();
        if(intent!=null){
            if(intent.getExtras()!=null) {
                if(intent.getExtras().getString("query")!=null)
                    b.putString("name", intent.getExtras().getString("query"));
                else
                    b.putLong("parent_id", intent.getExtras().getLong("parent_id"));
            }
        }
        setContentView(R.layout.activity_search_result);
        if (savedInstanceState == null) {
            SearchResultFragment searchResultFragment = new SearchResultFragment();
            searchResultFragment.setArguments(b);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, searchResultFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
