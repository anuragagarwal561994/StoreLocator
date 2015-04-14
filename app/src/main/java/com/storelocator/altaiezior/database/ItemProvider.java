package com.storelocator.altaiezior.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class ItemProvider extends ContentProvider {
    public static final String AUTHORITY = "com.storelocator.altaiezior.database.AUTHORITY";
    public static final String SCHEME = "content://";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        CategoryItem.addMatcherUris(sURIMatcher);
    }

    @Override
    public boolean onCreate() {
        return true;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {

            case CategoryItem.BASEITEMCODE:
                return CategoryItem.TYPE_ITEM;
            case CategoryItem.BASEURICODE:
                return CategoryItem.TYPE_DIR;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] args, String sortOrder) {
        Cursor result = null;
        final long id;
        final DatabaseHandler handler = DatabaseHandler.getInstance(getContext());

        switch (sURIMatcher.match(uri)) {

            case CategoryItem.BASEITEMCODE:
                id = Long.parseLong(uri.getLastPathSegment());
                result = handler.getCategoryItemCursor(id);
                result.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case CategoryItem.BASEURICODE:
                result = handler.getAllCategoryItemsCursor(selection, args, sortOrder);
                result.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        return result;
    }
}