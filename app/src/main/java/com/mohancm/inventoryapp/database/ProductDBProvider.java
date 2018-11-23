package com.mohancm.inventoryapp.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class ProductDBProvider extends ContentProvider {

    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;
    private ProductDBHelper productDBHelper;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        uriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        productDBHelper = new ProductDBHelper(getContext());
        return false;
    }

    private void validateContentValue(Uri uri, ContentValues contentValues) {

        // Checking if value is set for Product Name and it is null.
        if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME) && TextUtils.isEmpty(contentValues.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME)))
            throw new IllegalArgumentException("Product Name is not provided." + uri);

        // Checking if value is set for Product Price and it is null.
        if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE) && TextUtils.isEmpty(contentValues.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE)))
            throw new IllegalArgumentException("Product Price is not provided." + uri);

        // Checking if value is set for Product Quantity and it is null.
        if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY) && TextUtils.isEmpty(contentValues.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY)))
            throw new IllegalArgumentException("Product Quantity is not provided." + uri);

        // Checking if value is set for Supplier Name and it is null.
        if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME) && TextUtils.isEmpty(contentValues.getAsString(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME)))
            throw new IllegalArgumentException("Supplier Name is not provided." + uri);

        // Checking if value is set for Supplier phone and it is null.
        if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE) && TextUtils.isEmpty(contentValues.getAsString(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE)))
            throw new IllegalArgumentException("Supplier Contact Number is not provided." + uri);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase sqLiteDatabase = productDBHelper.getReadableDatabase();

        // Matching requested URI with predefined pattern.
        int match = uriMatcher.match(uri);

        Cursor cursor;
        switch (match) {
            case PRODUCTS:
                cursor = sqLiteDatabase.query(ProductContract.ProductEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry.COLUMN_PRODUCT_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.query(ProductContract.ProductEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query, Unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), ProductContract.ProductEntry.CONTENT_URI);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Invalid URI");
        }
    }

    private Uri insertProduct(Uri uri, ContentValues contentValues) {

        // database instance just to write data.
        SQLiteDatabase db = productDBHelper.getWritableDatabase();

        // Validating data.
        validateContentValue(uri, contentValues);


        long id = db.insert(ProductContract.ProductEntry.TABLE_NAME, null, contentValues);
        if (id == -1)
            throw new IllegalArgumentException("Unable to Insert." + uri);

        // Notifying data inserted.
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.withAppendedPath(uri, String.valueOf(id));
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = productDBHelper.getWritableDatabase();

        // Matching requested URI with predefined pattern.
        int match = uriMatcher.match(uri);

        int result;
        switch (match) {
            case PRODUCTS:
                result = sqLiteDatabase.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry.COLUMN_PRODUCT_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                result = sqLiteDatabase.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot delete, Invalid URI : " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = uriMatcher.match(uri);

        int result = 0;
        switch (match) {
            case PRODUCTS:
                result = updateProduct(uri, values, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry.COLUMN_PRODUCT_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                result = updateProduct(uri, values, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    private int updateProduct(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        // database instance just to write data.
        SQLiteDatabase sqLiteDatabase = productDBHelper.getWritableDatabase();

        int result = sqLiteDatabase.update(ProductContract.ProductEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }
}
