package com.storelocator.altaiezior;

import java.util.Collection;
import java.util.HashMap;

import android.app.Activity;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ActivityNotFoundException;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.storelocator.altaiezior.database.DatabaseHandler;
import com.storelocator.altaiezior.database.CategoryItem;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.storelocator.altaiezior.sync.SyncHelper;
import com.storelocator.altaiezior.sync.GetTokenTask;
import com.storelocator.altaiezior.sync.AccountDialog;

//TODO: have a look at this comment later
/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the
 * ListView with a GridView.
 * <p />
 */
public class CategoryFragment extends Fragment {

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private CursorAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CategoryFragment() {
    }

    OnCategorySelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnCategorySelectedListener{
        public void onCategorySelected(CategoryItem categoryItem);
        public void chooseFromDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.list_item, null, new String[] {
                CategoryItem.COLUMN_NAME }, new int[] {
                android.R.id.text1 }, 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_product, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_product_list, container, false);
        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long id) {
                final CategoryItem categoryItem = new CategoryItem((Cursor) mAdapter.getItem(position));
                mCallback.onCategorySelected(categoryItem);
            }
        });

        final Button mSearchButton = (Button) view.findViewById(R.id.product_search_button);
        final EditText mSearchEditText = (EditText) view.findViewById(R.id.search_edit_box);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(getActivity(), SearchResult.class);
                searchIntent.putExtra("query", mSearchEditText.getText().toString());
                startActivity(searchIntent);
            }
        });

        // Load content
        getLoaderManager().initLoader(0, null, new LoaderCallbacks<Cursor>() {

            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(getActivity(), CategoryItem.URI(),
                        CategoryItem.FIELDS, CategoryItem.COLUMN_DELETED + " IS 0 AND "
                        + CategoryItem.COLUMN_PARENT_ID + " IS 0 ", null,
                        CategoryItem.COLUMN_TIMESTAMP + " DESC");
            }

            @Override
            public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
                mAdapter.swapCursor(c);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> arg0) {
                mAdapter.swapCursor(null);
            }
        });

        return view;
    }

    void deleteItems(Collection<CategoryItem> items) {
        for (CategoryItem item: items) {
            getActivity().getContentResolver().delete(item.getUri(), null, null);
        }
    }

    public void updateCategoryList(Long parent_id){
        DatabaseHandler db = DatabaseHandler.getInstance(getActivity());
        Cursor c = db.getCategoriesFromParent(parent_id);
        if(c.getCount()>0){
            mAdapter.swapCursor(c);
            mAdapter.notifyDataSetChanged();
        }
        else{
            mCallback.chooseFromDialog();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnCategorySelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCategorySelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = false;
        switch (item.getItemId()) {
            case R.id.action_sync:
                final String email  = PreferenceManager.getDefaultSharedPreferences(
                        getActivity()).getString(SyncHelper.KEY_ACCOUNT, null);
                if(email!=null){
                    Toast.makeText(getActivity(), R.string.syncing_, Toast.LENGTH_SHORT).show();
                    SyncHelper.manualSync(getActivity());
                }
                else{
                    if(null == SyncHelper.getSavedAccountName(getActivity())){
                        final Account[] accounts = AccountManager.get(getActivity())
                                .getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
                        if(accounts.length==1) {
                            new GetTokenTask((SearchProduct) getActivity(), accounts[0].name,
                                    SyncHelper.SCOPE).execute();
                        }
                        else if (accounts.length>1){
                            DialogFragment dialog = new AccountDialog();
                            dialog.show(getFragmentManager(), "account_dialog");
                        }
                    }
                }
                break;
            case R.id.action_user_detail:
                startActivity(new Intent(getActivity(), UserDetail.class));
                getActivity().finish();
                break;
            case R.id.action_logout:
                SharedPreferences.Editor loginPreference = getActivity().getSharedPreferences(
                        "Login", 0).edit();
                loginPreference.putBoolean("loggedIn", false).commit();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return result;
    }
}
