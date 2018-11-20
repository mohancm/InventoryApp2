package com.mohancm.inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mohancm.inventoryapp.database.ProductContract.ProductEntry;
import com.mohancm.inventoryapp.database.ProductDBHelper;


public class MainActivity extends AppCompatActivity {

    private ProductDBHelper productDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        productDBHelper = new ProductDBHelper(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                break;
            case R.id.action_delete_all_entries:
                break;
        }
        return true;
    }

    private Cursor queryData() {
        SQLiteDatabase database = productDBHelper.getReadableDatabase();
        String[] projections = {
                ProductEntry.COLUMN_PRODUCT_ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE,
        };

        Cursor cursor;
        cursor = database.query(ProductEntry.TABLE_NAME,
                projections,
                null,
                null,
                null,
                null,
                null,
                null);

        return cursor;

    }


    private void displayData() {
        Cursor cursor = queryData();
        TextView displayView = findViewById(R.id.products_tv);
        displayView.setText(getString(R.string.the_books_table_contains));
        displayView.append(" " + cursor.getCount() + " ");
        displayView.append(getString(R.string.products) + "\n\n");

        int productIdColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_ID);
        int productNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int productPriceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int productQuantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int productSupplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
        int productSupplierContactColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE);

        try {
            while (cursor.moveToNext()) {
                int currentId = cursor.getInt(productIdColumnIndex);
                String currentProductName = cursor.getString(productNameColumnIndex);
                int currentProductPrice = cursor.getInt(productPriceColumnIndex);
                int currentProductQuantity = cursor.getInt(productQuantityColumnIndex);
                String currentProductSupplierName = cursor.getString(productSupplierNameColumnIndex);
                String currentProductSupplierContact = cursor.getString(productSupplierContactColumnIndex);

                displayView.append("\n" + ProductEntry._ID + " : " + currentId + "\n" +
                        ProductEntry.COLUMN_PRODUCT_NAME + " : " + currentProductName + "\n" +
                        ProductEntry.COLUMN_PRODUCT_PRICE + " : " + currentProductPrice + "\n" +
                        ProductEntry.COLUMN_PRODUCT_QUANTITY + " : " + currentProductQuantity + "\n" +
                        ProductEntry.COLUMN_SUPPLIER_NAME + " : " + currentProductSupplierName + "\n" +
                        ProductEntry.COLUMN_SUPPLIER_PHONE + " : " + currentProductSupplierContact + "\n");
            }
        } finally {
            cursor.close();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        displayData();
    }
}
