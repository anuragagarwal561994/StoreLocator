package com.storelocator.altaiezior.database;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * Represents Link in the database.
 *
 */
public class CategoryItem extends DBItem {
    public static final String TABLE_NAME = "Categories";

    public CategoryItem() {
        super();
    }

    public CategoryItem(final Cursor cursor) {
        super();
        // Projection expected to match FIELDS array
        this._id = cursor.getLong(0);
        this.name = cursor.getString(1);
        this.parent_id = cursor.getLong(2);
        this.timestamp = cursor.getString(3);
        this.synced = cursor.getLong(4);
    }

    public static Uri URI() {
        return Uri.withAppendedPath(
                Uri.parse(ItemProvider.SCHEME
                        + ItemProvider.AUTHORITY), TABLE_NAME);
    }

    // Column names
    public static final String COLUMN__ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PARENT_ID = "parent_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_SYNCED = "synced";

    // For database projection so order is consistent
    public static final String[] FIELDS = { COLUMN__ID, COLUMN_NAME, COLUMN_PARENT_ID,
            COLUMN_TIMESTAMP, COLUMN_SYNCED };

    public long _id = -1;
    public String name;
    public long parent_id;
    public String timestamp = null;
    public long synced = 0;

    public static final int BASEURICODE = 0x3b109c7;
    public static final int BASEITEMCODE = 0x87a22b7;

    public static void addMatcherUris(UriMatcher sURIMatcher) {
        sURIMatcher.addURI(ItemProvider.AUTHORITY, TABLE_NAME, BASEURICODE);
        sURIMatcher.addURI(ItemProvider.AUTHORITY, TABLE_NAME + "/#", BASEITEMCODE);
    }

    public static final String TYPE_DIR = "vnd.android.cursor.dir/vnd.storelocator." + TABLE_NAME;
    public static final String TYPE_ITEM = "vnd.android.cursor.item/vnd.storelocator." + TABLE_NAME;

    public ContentValues getContent() {
        ContentValues values = new ContentValues();

        values.put(COLUMN__ID, _id);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PARENT_ID, parent_id);
        if (timestamp != null) values.put(COLUMN_TIMESTAMP, timestamp);
        values.put(COLUMN_SYNCED, synced);

        return values;
    }

    public String getTableName() {
        return TABLE_NAME;
    }

    public String[] getFields() {
        return FIELDS;
    }

    public long getId() {
        return _id;
    }

    public void setId(final long id) {
        _id = id;
    }

    //TODO: add foreign key reference
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME
                    +"  ("+COLUMN__ID+" INTEGER PRIMARY KEY,"
                    +"  "+COLUMN_NAME+" TEXT NOT NULL,"
                    +"  "+COLUMN_PARENT_ID+" INTEGER NOT NULL,"
                    +"  "+COLUMN_TIMESTAMP+" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                    +"  "+COLUMN_SYNCED+" INTEGER NOT NULL DEFAULT 0,"
                    +""
                    +"  UNIQUE ("+COLUMN_NAME+") ON CONFLICT REPLACE)";
}