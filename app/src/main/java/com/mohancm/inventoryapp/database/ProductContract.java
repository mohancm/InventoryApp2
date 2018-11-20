package com.mohancm.inventoryapp.database;

import android.provider.BaseColumns;

public class ProductContract {


    private ProductContract() {

    }

    public static abstract class ProductEntry implements BaseColumns {

        private ProductEntry() {

        }
        public static final String TABLE_NAME = "Products";

        public static final String COLUMN_PRODUCT_ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRODUCT_PRICE = "product_price";
        public static final String COLUMN_PRODUCT_QUANTITY = "product_quantity";

        public static final String COLUMN_SUPPLIER_NAME = "product_supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE = "product_supplier_phone";
    }
}