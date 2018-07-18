package com.example.android.bookstoreapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.bookstoreapp.data.BookContract;

/**
 * Created by giorgosnty on 27/6/2018.
 */

public class BookCursorAdapter extends CursorAdapter{

    private final MainActivity activity;

    public BookCursorAdapter(MainActivity context, Cursor c) {

        super(context, c,0);
        this.activity = context;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView titleTextView =  (TextView) view.findViewById(R.id.title);
        TextView priceTextView =  (TextView) view.findViewById(R.id.price);
         TextView quantityTextView =  (TextView) view.findViewById(R.id.quantity);

        int titleColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_QUANTINTY);

        String bookTitle = cursor.getString(titleColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        final int quantity =cursor.getInt(quantityColumnIndex);



        titleTextView.setText(bookTitle);
        priceTextView.setText(price);
        quantityTextView.setText(String.valueOf(quantity));


        Button sale_btn =  (Button) view.findViewById(R.id.sale);

        final long id  =cursor.getLong(cursor.getColumnIndex(BookContract.BookEntry._ID));


        //listener for the sale button
        sale_btn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                activity.clickOnSale(id,quantity);

            }
        });

        //listener for the details
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.clickOnViewItem(id);
            }
        });



    }
}
