package com.storelocator.altaiezior;

import com.storelocator.altaiezior.database.DatabaseHandler;
import com.storelocator.altaiezior.database.CategoryItem;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class CategoryDatabaseService extends IntentService {

    private static final String ACTION_ADD = "com.storelocator.altaiezior.action.ADD";
    private static final String ACTION_DELETE = "com.storelocator.altaiezior.action.DELETE";
    private static final String EXTRA_CATEGORY = "com.storelocator.altaiezior.extra.CATEGORY";

    /**
     * Starts this service to perform action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void addCategory(Context context, Long _id, String name, Long parent_id) {
        Intent intent = new Intent(context, CategoryDatabaseService.class);
        Bundle b = new Bundle();
        b.putLong("_id", _id);
        b.putString("name", name);
        b.putLong("parent_id", parent_id);
        intent.setAction(ACTION_ADD).putExtra(EXTRA_CATEGORY, b);
        context.startService(intent);
    }

    public static void removeCategory(Context context, Long _id) {
        Intent intent = new Intent(context, CategoryDatabaseService.class);
        intent.setAction(ACTION_DELETE).putExtra(EXTRA_CATEGORY, _id);
        context.startService(intent);
    }

    public CategoryDatabaseService() {
        super("CategoryDatabaseService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_ADD.equals(action)) {
                addCategory(intent.getBundleExtra(EXTRA_CATEGORY));
            }
            else if(ACTION_DELETE.equals(action)) {
                removeCategory(intent.getLongExtra(EXTRA_CATEGORY, -1));
            }
        }
    }

    private void removeCategory(final Long _id) {
        if (_id<0){
            return;
        }
        CategoryItem currentCategoryItem = DatabaseHandler.getInstance(this).getCategoryItem(_id);
        if (currentCategoryItem != null)
            DatabaseHandler.getInstance(this).deleteItem(currentCategoryItem);
    }

    private void addCategory(final Bundle b) {
        if (b.getLong("_id")<0){
            return;
        }

        if (b.getString("name") == null || b.getString("name").isEmpty()){
            return;
        }

        if (b.getLong("parent_id") < 0){
            return;
        }

        CategoryItem categoryItem = new CategoryItem();
        categoryItem._id = b.getLong("_id");
        categoryItem.name = b.getString("name");
        categoryItem.parent_id = b.getLong("parent_id");

        DatabaseHandler.getInstance(this).putItem(categoryItem);
    }
}
