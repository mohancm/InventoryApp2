package com.mohancm.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mohancm.inventoryapp.database.ProductContract.ProductEntry;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int LOADER_ID = 0;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView productsList = findViewById(R.id.productsList);
        productAdapter = new ProductAdapter(this, null);
        productsList.setEmptyView(findViewById(R.id.emptyView));

        productsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.setData(ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id));
                startActivity(intent);
            }
        });

        productsList.setAdapter(productAdapter);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Initiating Loader.
        getLoaderManager().initLoader(LOADER_ID, null, this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Handling menu item clicks.
        switch (menuItem.getItemId()) {
            case R.id.action_insert_dummy_data:
                insetData();
                break;
            case R.id.action_delete_all_entries:
                // Displaying confirmation dialog to confirm delete all.
                showDeleteConfirmationDialog();
                break;
            case R.id.action_settings:
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                // starting settings activity.
                startActivity(i);
                break;
        }
        return true;
    }

    private void deleteALlData() {
        int i = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        Toast.makeText(this, i + " " + getString(R.string.main_delete_all), Toast.LENGTH_SHORT).show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_confirm_text));
        // Handling positive response.
        builder.setPositiveButton(getString(R.string.delete_confirm_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteALlData();
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


    private void insetData() {

        // Creating contectValues.
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductEntry.COLUMN_PRODUCT_NAME, getString(R.string.dummy_productName));
        contentValues.put(ProductEntry.COLUMN_PRODUCT_PRICE, getString(R.string.dummy_productPrice));
        contentValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, getString(R.string.dummy_productQuantity));
        contentValues.put(ProductEntry.COLUMN_SUPPLIER_NAME, getString(R.string.dummy_supName));
        contentValues.put(ProductEntry.COLUMN_SUPPLIER_PHONE, getString(R.string.dummy_supContact));
        getContentResolver().insert(ProductEntry.CONTENT_URI, contentValues);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Creating cursor loader to get all product data from database.
        // order is descending new products will come at top.
        return new CursorLoader(this, ProductEntry.CONTENT_URI, null, null, null, ProductEntry.COLUMN_PRODUCT_ID + " desc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        productAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productAdapter.swapCursor(null);
    }
}
