package com.mohancm.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mohancm.inventoryapp.database.ProductContract;

public class ProductAdapter extends CursorAdapter {
    public ProductAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_items, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final int producID = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_ID));
        final int currentQuantity = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY));
        ((TextView) view.findViewById(R.id.prdName)).setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME)));
        ((TextView) view.findViewById(R.id.prdPrice)).setText(context.getString(R.string.rupeeSymbol) + " " + cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE)));
        final TextView txtQuantity = view.findViewById(R.id.prdQty);
        txtQuantity.setText(context.getString(R.string.quantityAvailableText) + " " + String.valueOf(currentQuantity));

        // Using sharedPreferance getting minimum warn quality
        String minimum = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.settings_minimum_safe_quantity_key), context.getString(R.string.settings_minimum_safe_quantity_value));

        // Checking the currentQuantity
        if (currentQuantity < Integer.valueOf(minimum)) {
            txtQuantity.setTextColor(context.getResources().getColor(R.color.quantityWarning));
        }
        if (currentQuantity <= 0) {
            txtQuantity.setTextColor(context.getResources().getColor(R.color.noQuantity));
        }

        // Button with onClickListener.
        Button btnSale = view.findViewById(R.id.btnSale);
        btnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentQuantity > 0) {
                    // Decrementing the quantity.
                    int newQuantity = currentQuantity - 1;

                    // Creating URI for specific product for updating new Quantity.
                    Uri productUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, producID);

                    // Creating contentValue to update Quantity only.
                    ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);

                    // Updating product by using contentResolver.
                    context.getContentResolver().update(productUri, values, null, new String[]{String.valueOf(producID)});
                } else
                    Toast.makeText(context, context.getString(R.string.main_negative_quantity), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
