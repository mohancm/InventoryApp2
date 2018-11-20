package com.mohancm.inventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.mohancm.inventoryapp.database.ProductDBHelper;
import com.mohancm.inventoryapp.database.ProductContract.ProductEntry;

public class EditorActivity extends AppCompatActivity {

    public ProductDBHelper productDBHelper;
    private EditText productNameEditText;
    private EditText productPriceEditText;
    private EditText productQuantityEditText;
    private EditText productSupplierNameEditText;
    private EditText productSupplierContactEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        productNameEditText = findViewById(R.id.product_name);
        productPriceEditText = findViewById(R.id.poduct_price);
        productQuantityEditText = findViewById(R.id.product_quantity);
        productSupplierNameEditText = findViewById(R.id.supplier_name);
        productSupplierContactEditText = findViewById(R.id.supplier_contact);
        productDBHelper = new ProductDBHelper(this);

    }
    private void insertData() {
        String productName;
        if (TextUtils.isEmpty(productNameEditText.getText())){
            productNameEditText.setError(getString(R.string.required_field));
            return;
        } else {
            productName = productNameEditText.getText().toString().trim();
        }

        String productPrice;
        if (TextUtils.isEmpty(productPriceEditText.getText())){
            productPriceEditText.setError(getString(R.string.required_field));
            return;
        } else {
            productPrice = productPriceEditText.getText().toString().trim();
        }

        String productQuantity;
        if (TextUtils.isEmpty(productQuantityEditText.getText())){
            productQuantityEditText.setError(getString(R.string.required_field));
            return;
        } else {
            productQuantity = productQuantityEditText.getText().toString().trim();
        }

        String supplierName;
        if (TextUtils.isEmpty(productSupplierNameEditText.getText())){
            productSupplierNameEditText.setError(getString(R.string.required_field));
            return;
        } else {
            supplierName = productSupplierNameEditText.getText().toString().trim();
        }

        String supplierContact;
        if (TextUtils.isEmpty(productSupplierContactEditText.getText())){
            productSupplierContactEditText.setError(getString(R.string.required_field));
            return;
        } else {
            supplierContact = productSupplierContactEditText.getText().toString().trim();
        }

        int productPriceInt = Integer.parseInt(productPrice);
        if (productPriceInt < 0){
            productPriceEditText.setError(getString(R.string.price_cannot_be_negative));
            Toast.makeText(this, getString(R.string.price_cannot_be_negative),Toast.LENGTH_SHORT).show();
            return;
        }

        int productQuantityInt = Integer.parseInt(productQuantity);
        if (productQuantityInt < 0) {
            productQuantityEditText.setError(getString(R.string.quantity_cannot_be_negative));
            Toast.makeText(this,getString(R.string.quantity_cannot_be_negative), Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase database = productDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductEntry.COLUMN_PRODUCT_NAME,productName);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_PRICE,productPrice);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY,productQuantity);
        contentValues.put(ProductEntry.COLUMN_SUPPLIER_NAME,supplierName);
        contentValues.put(ProductEntry.COLUMN_SUPPLIER_PHONE,supplierContact);

        long newRowId = database.insert(ProductEntry.TABLE_NAME, null, contentValues);
        if (newRowId == -1){
            Toast.makeText(this, getString(R.string.error_saving_book),Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, getString(R.string.product_saved) + " " + newRowId, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()){
           case R.id.action_save:
               insertData();
               return true;
           case android.R.id.home:
               NavUtils.navigateUpFromSameTask(this);
               return true;
       }
       return super.onOptionsItemSelected(item);
    }
}
