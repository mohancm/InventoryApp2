package com.mohancm.inventoryapp.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ProductContract {

    public static final String CONTENT_AUTHORITY = "com.mohancm.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";


    public static abstract class ProductEntry implements BaseColumns {

        public static final String TABLE_NAME = "Products";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String COLUMN_PRODUCT_ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRODUCT_PRICE = "product_price";
        public static final String COLUMN_PRODUCT_QUANTITY = "product_quantity";

        public static final String COLUMN_SUPPLIER_NAME = "product_supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE = "product_supplier_phone";
    }
}