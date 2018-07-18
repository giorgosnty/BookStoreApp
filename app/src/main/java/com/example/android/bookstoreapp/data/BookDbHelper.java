package com.example.android.bookstoreapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.bookstoreapp.data.BookContract.BookEntry.TABLE_NAME;

/**
 * Created by giorgosnty on 3/6/2018.
 */

public class BookDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bookstore.db";
    private static final int DATABASE_VERSION = 1;

    public BookDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BOOKS_TABLE =  "CREATE TABLE " + TABLE_NAME + " ("
                + BookContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookContract.BookEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + BookContract.BookEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + BookContract.BookEntry.COLUMN_PRODUCT_QUANTINTY + " INTEGER NOT NULL DEFAULT 0, "
                + BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " TEXT NOT NULL,"
                + BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE + " TEXT NOT NULL DEFAULT 0" + ");";

        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //to ne updated in another version
    }

    public void sellBook(long id, int quantity) {

        SQLiteDatabase db = getWritableDatabase();
        int reducedQuantity = 0;
        if (quantity > 0) {

            reducedQuantity = quantity -1;
        }

        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_PRODUCT_QUANTINTY, reducedQuantity);
        String selection = BookContract.BookEntry._ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(id) };
        db.update(TABLE_NAME, values, selection, selectionArgs);

    }

    public Cursor queryData(){

        // Create and/or open a database to read from it
        SQLiteDatabase db = getReadableDatabase();

        String[] projection={
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_PRODUCT_NAME,
                BookContract.BookEntry.COLUMN_PRODUCT_PRICE,
                BookContract.BookEntry.COLUMN_PRODUCT_QUANTINTY,
                BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE
        };

        Cursor cursor =db.query(TABLE_NAME,projection,null,null,null,null,null);

        return cursor;
    }


}
