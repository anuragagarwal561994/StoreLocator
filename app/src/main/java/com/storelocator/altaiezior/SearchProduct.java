package com.storelocator.altaiezior;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.storelocator.altaiezior.database.CategoryItem;
import com.storelocator.altaiezior.sync.GetTokenTask;
import com.storelocator.altaiezior.sync.AccountDialog;
import com.storelocator.altaiezior.sync.SyncHelper;

import android.accounts.AccountManager;
import android.accounts.Account;
import android.app.DialogFragment;
import android.app.Dialog;
import android.support.v4.app.NavUtils;
import android.util.Log;

import java.util.Stack;

public class SearchProduct extends Activity
    implements CategoryFragment.OnCategorySelectedListener {

    private Stack<CategoryItem> categoryStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryStack = new Stack<CategoryItem>();
        setContentView(R.layout.activity_search_product);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new CategoryFragment())
                    .commit();
        }
        if(null==SyncHelper.getSavedAccountName(this)){
            final Account[] accounts = AccountManager.get(this)
                    .getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
            if(accounts.length == 1) {
                new GetTokenTask(this, accounts[0].name, SyncHelper.SCOPE).execute();
            }
            else if(accounts.length>1){
                DialogFragment dialog = new AccountDialog();
                dialog.show(getFragmentManager(), "account_dialog");
            }
        }
    }
    public void showErrorDialog(final int code){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog d = GooglePlayServicesUtil
                        .getErrorDialog(code,
                                SearchProduct.this,
                                GetTokenTask.REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                d.show();
            }
        });
    }
    @Override
    public void onBackPressed(){
        if(!categoryStack.isEmpty()) {
            categoryStack.pop();
            if(!categoryStack.isEmpty())
                onCategorySelected(categoryStack.pop());
            else
                onCategorySelected(null);
        }
        else {
            NavUtils.navigateUpFromSameTask(this);
        }
    }

    //TODO: On dialog cancel
    @Override
    public void onCategorySelected(CategoryItem categoryItem) {
        if(categoryItem != null)
            categoryStack.push(categoryItem);
        CategoryFragment categoryFragment = (CategoryFragment)
                getFragmentManager().findFragmentById(R.id.container);
        if(categoryFragment != null){
            if(categoryStack.isEmpty())
                categoryFragment.updateCategoryList(0L);
            else
                categoryFragment.updateCategoryList(categoryStack.peek().getId());
        }
    }

    public void removeLastFromStack(){
        if(!categoryStack.isEmpty())
            categoryStack.pop();
    }
}
