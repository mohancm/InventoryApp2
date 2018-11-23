package com.mohancm.inventoryapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mohancm.inventoryapp.database.ProductContract;

public class DetailActivity extends AppCompatActivity {

    private Uri productUri;
    private TextView prdNameTextView;
    private TextView prdPriceTextView;
    private TextView prdQuantityTextView;
    private TextView supNameTextView;
    private TextView supContactTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Receiving the URI sent from MainActivity.
        productUri = getIntent().getData();

        // Getting cursor for given URI.
        updateUT();
    }

    private void updateUT() {
        Cursor cursor = getContentResolver().query(productUri, null, null, null, null);
        try {
            if (cursor.moveToNext()) {
                prdNameTextView = findViewById(R.id.prdName);
                prdQuantityTextView = findViewById(R.id.prdQty);
                prdPriceTextView = findViewById(R.id.prdPrice);
                supContactTextView = findViewById(R.id.supContact);
                supNameTextView = findViewById(R.id.supName);

                // Setting values to respective TextViews.
                prdNameTextView.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME)));
                prdQuantityTextView.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY)));
                prdPriceTextView.setText(getString(R.string.rupeeSymbol) + " " + cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE)));
                supNameTextView.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME)));
                supContactTextView.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE)));
            }
        } finally {
            // Closing cursor.
            cursor.close();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUT();
    }

    public void increment(View view) {
        int currentQuntity = Integer.valueOf(prdQuantityTextView.getText().toString());
        ContentValues contentValues = new ContentValues();

        // Extracting SharedPreferance value for increment.
        String incrementValue = PreferenceManager.getDefaultSharedPreferences(DetailActivity.this).getString(getString(R.string.settings_default_quantityIncrement_key), getString(R.string.settings_default_quantityIncrement_value));
        contentValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, currentQuntity + Integer.valueOf(incrementValue));

        // Updating database.
        getContentResolver().update(productUri, contentValues, null, new String[]{ProductContract.ProductEntry.COLUMN_PRODUCT_ID});

        // updating new quantity to TextView.
        prdQuantityTextView.setText(String.valueOf(currentQuntity + Integer.valueOf(incrementValue)));
    }

    public void decrement(View view) {
        int currentQuntity = Integer.valueOf(prdQuantityTextView.getText().toString());
        if (currentQuntity != 0) {
            // Extracting SharedPreferance value for increment.
            String decrementValue = PreferenceManager.getDefaultSharedPreferences(DetailActivity.this).getString(getString(R.string.settings_default_quantityIncrement_key), getString(R.string.settings_default_quantityIncrement_value));

            ContentValues contentValues = new ContentValues();

            if ((currentQuntity - Integer.valueOf(decrementValue)) < 0)
                contentValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, 0);
            else
                contentValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, currentQuntity - Integer.valueOf(decrementValue));

            // Updating value in database.
            getContentResolver().update(productUri, contentValues, null, null);
            prdQuantityTextView.setText(String.valueOf(contentValues.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY)));
        } else {
            Toast.makeText(DetailActivity.this, getString(R.string.detail_negative_quantity), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                break;
            case R.id.action_edit:
                Intent i = new Intent(DetailActivity.this, EditorActivity.class);
                i.setData(productUri);
                startActivity(i);
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return true;
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_confirm_text));
        // Handling positive response.
        builder.setPositiveButton(getString(R.string.delete_confirm_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getContentResolver().delete(productUri, null, null);
                finish();
            }
        });
        // Handling Negative Response.
        builder.setNegativeButton(R.string.delete_confirm_negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_items, menu);
        return true;
    }

    // Handling order now button click.
    public void orderNow(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + supContactTextView.getText().toString()));
        startActivity(intent);
    }
}
