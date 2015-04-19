package com.storelocator.altaiezior.sync;

import retrofit.RetrofitError;

import com.storelocator.altaiezior.api.CategoriesServer;
import com.storelocator.altaiezior.database.DatabaseHandler;
import com.storelocator.altaiezior.database.CategoryItem;
import com.storelocator.altaiezior.api.CategoriesServer.CategoryItems;
import com.storelocator.altaiezior.api.CategoriesServer.CategoryMSG;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "CategorySyncAdapter";
    private static final String KEY_LASTSYNC = "key_lastsync";

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public SyncAdapter(Context context, boolean autoInitialize,
                       boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        try {
            // Need to get an access token first
            final String token = SyncHelper.getAuthToken(getContext(),
                    account.name);

            if (token == null) {
                Log.e(TAG, "Token was null. Aborting sync");
                // Sync is rescheduled by SyncHelper
                return;
            }

            // Just to make sure. Can happen if sync happens in background first
            // time
            if (null == SyncHelper.getSavedAccountName(getContext())) {
                PreferenceManager.getDefaultSharedPreferences(getContext())
                        .edit().putString(SyncHelper.KEY_ACCOUNT, account.name)
                        .commit();
            }
            // token should be good. Transmit

            final CategoriesServer server = SyncHelper.getRESTAdapter();
            DatabaseHandler db = DatabaseHandler.getInstance(getContext());

            // Upload stuff
            for (CategoryItem item : db.getAllCategoryItems(CategoryItem.COLUMN_SYNCED
                    + " IS 0 OR " + CategoryItem.COLUMN_DELETED + " IS 1", null, null)) {
                if (item.deleted != 0) {
                    // Delete the item
                    server.deleteCategory(token, item._id);

                    syncResult.stats.numDeletes++;
                    db.deleteItem(item);
                }
                else {
                    server.addCategory(token, new CategoryMSG(item));
                    syncResult.stats.numInserts++;
                    item.synced = 1;
                    db.putItem(item);
                }
            }

            // Download stuff - but only if this is not an upload-only sync
            if (!extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, false)) {
                // Check if we synced before
                final String lastSync = PreferenceManager
                        .getDefaultSharedPreferences(getContext()).getString(
                                KEY_LASTSYNC, null);

                final CategoryItems items;
                if (lastSync != null && !lastSync.isEmpty()) {
                    items = server.listCategories(token, "true", lastSync);
                }
                else {
                    items = server.listCategories(token, "false", null);
                }
                db.setForeignKeyConstraint(false);
                if (items != null && items.getCategories() != null) {
                    for (CategoryMSG msg : items.getCategories()) {
                        Log.d(TAG, "got category:" + msg.getName() + ", _id: " + msg.get_id());
                        final CategoryItem item = msg.toDBItem();
                        if (msg.isDeleted()) {
                            Log.d(TAG, "Deleting:" + msg.getName());
                            db.deleteItem(item);
                        }
                        else {
                            Log.d(TAG, "Adding category:" + item.name);
                            item.synced = 1;
                            db.putItem(item);
                        }
                    }
                }
                db.setForeignKeyConstraint(true);

                //Save sync timestamp
                PreferenceManager.getDefaultSharedPreferences(getContext())
                        .edit().putString(KEY_LASTSYNC, items.getLatestTimestamp())
                        .commit();
            }
        }
        catch (RetrofitError e) {
            Log.d(TAG, "" + e);
            final int status;
            if (e.getResponse() != null) {
                Log.e(TAG, "" + e.getResponse().getStatus() + "; "
                        + e.getResponse().getReason());
                status = e.getResponse().getStatus();
            }
            else {
                status = 999;
            }
            // An HTTP error was encountered.
            switch (status) {
                case 401: // Unauthorized
                    syncResult.stats.numAuthExceptions++;
                    break;
                case 404: // No such item, should never happen, programming error
                case 415: // Not proper body, programming error
                case 400: // Didn't specify url, programming error
                    syncResult.databaseError = true;
                    break;
                default: // Default is to consider it a networking problem
                    syncResult.stats.numIoExceptions++;
                    break;
            }
        }
    }
}