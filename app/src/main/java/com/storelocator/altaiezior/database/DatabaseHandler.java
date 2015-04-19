package com.storelocator.altaiezior.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database handler, SQLite wrapper and ORM layer.
 *
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "StoreLocator";
    private final Context context;

    private static DatabaseHandler instance = null;

    public synchronized static DatabaseHandler getInstance(Context context) {
        if (instance == null)
            instance = new DatabaseHandler(context.getApplicationContext());
        return instance;
    }

    public DatabaseHandler(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context.getApplicationContext();
    }

    public void setForeignKeyConstraint(boolean on){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys="+(on?"ON":"OFF")+";");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            // This line requires android16
            // db.setForeignKeyConstraintsEnabled(true);
            // This line works everywhere though
            db.execSQL("PRAGMA foreign_keys=ON;");

            // Create temporary triggers
            DatabaseTriggers.createTemp(db);
        }
    }

    @Override
    public synchronized void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + CategoryItem.TABLE_NAME);
        db.execSQL(CategoryItem.CREATE_TABLE);

        // Create Triggers
        DatabaseTriggers.create(db);
    }

    // Upgrading database
    @Override
    public synchronized void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Try to drop and recreate. You should do something clever here
        onCreate(db);
    }

    // Convenience methods
    public synchronized boolean putItem(final DBItem item) {
        boolean success = false;
        int result = 0;
        final SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues values = item.getContent();

        if (item.getId() > -1) {
            result += db.update(item.getTableName(), values,
                    DBItem.COLUMN_ID + " IS ?",
                    new String[] { String.valueOf(item.getId()) });
        }

        // Update failed or wasn't possible, insert instead
        if (result < 1) {
            final long id = db.insert(item.getTableName(), null, values);

            if (id > 0) {
                item.setId(id);
                success = true;
            }
        }
        else {
            success = true;
        }

        if (success) {
            item.notifyProvider(context);
        }
        return success;
    }

    public synchronized int deleteItem(CategoryItem item) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final int result = db.delete(item.getTableName(), CategoryItem.COLUMN__ID
                + " IS ?", new String[] { Long.toString(item._id) });

        if (result > 0) {
            item.notifyProvider(context);
        }

        return result;
    }

    public Cursor getCategoriesFromParent(final long parent_id){
        final SQLiteDatabase db = this.getReadableDatabase();
        final Cursor cursor = db.query(CategoryItem.TABLE_NAME,
                CategoryItem.FIELDS, CategoryItem.COLUMN_PARENT_ID + " IS ?",
                new String[] {String.valueOf(parent_id)}, null, null, null, null);
        return cursor;
    }

    public synchronized Cursor getCategoryItemCursor(final long id) {
        final SQLiteDatabase db = this.getReadableDatabase();
        final Cursor cursor = db.query(CategoryItem.TABLE_NAME,
                CategoryItem.FIELDS, CategoryItem.COLUMN__ID + " IS ?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        return cursor;
    }

    public synchronized CategoryItem getCategoryItem(final long id) {
        final Cursor cursor = getCategoryItemCursor(id);
        final CategoryItem result;
        if (cursor.moveToFirst()) {
            result = new CategoryItem(cursor);
        }
        else {
            result = null;
        }

        cursor.close();
        return result;
    }

    public synchronized Cursor getAllCategoryItemsCursor(final String selection,
                                                     final String[] args,
                                                     final String sortOrder) {
        final SQLiteDatabase db = this.getReadableDatabase();

        final Cursor cursor = db.query(CategoryItem.TABLE_NAME,
                CategoryItem.FIELDS, selection, args, null, null, sortOrder, null);

        return cursor;
    }

    public synchronized List<CategoryItem> getAllCategoryItems(final String selection,
                                                       final String[] args,
                                                       final String sortOrder) {
        final List<CategoryItem> result = new ArrayList<CategoryItem>();

        final Cursor cursor = getAllCategoryItemsCursor(selection, args, sortOrder);

        while (cursor.moveToNext()) {
            CategoryItem q = new CategoryItem(cursor);
            result.add(q);
        }

        cursor.close();
        return result;
    }

}