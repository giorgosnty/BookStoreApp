package com.example.android.bookstoreapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by giorgosnty on 28/6/2018.
 */

public class BookProvider extends ContentProvider {


    /** Tag for the log messages */
    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the books table */
    private static final int BOOKS = 100;

    /** URI matcher code for the content URI for a single book in the pets table */
    private static final int BOOK_ID  = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer.
    static {


        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);


        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }


    /** Database helper object */
    private BookDbHelper mDbHelper;

    @Override
    public boolean onCreate() {

        mDbHelper = new BookDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch(match){
            case BOOKS:

                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unkown uri");
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookContract.BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookContract.BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues contentValues) {

        String title = contentValues.getAsString(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
        if(title == null){
            throw new IllegalArgumentException("Book requires a title");
        }

        Integer price = contentValues.getAsInteger(BookContract.BookEntry.COLUMN_PRODUCT_PRICE);
        if(price == null&& price<0){
            throw new IllegalArgumentException("Book requires a valid price");
        }

        Integer quantity = contentValues.getAsInteger(BookContract.BookEntry.COLUMN_PRODUCT_QUANTINTY);
        if(quantity == null&& quantity<0){
            throw new IllegalArgumentException("Book requires a valid quantity");
        }

        String supplierName = contentValues.getAsString(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        if(supplierName == null){
            throw new IllegalArgumentException("Book requires a suppliers name");
        }

        String supplierPhone = contentValues.getAsString(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);
        if(supplierPhone == null){
            throw new IllegalArgumentException("Book requires a phone for supplier");
        }



        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(BookContract.BookEntry.TABLE_NAME, null, contentValues);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }


        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:

                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        //here we will check for validity if there are the values.

        if (contentValues.containsKey(BookContract.BookEntry.COLUMN_PRODUCT_NAME)) {
            String name = contentValues.getAsString(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Book requires a title");
            }
        }


        if (contentValues.containsKey(BookContract.BookEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = contentValues.getAsInteger(BookContract.BookEntry.COLUMN_PRODUCT_PRICE);
            if (price == null || price<0) {
                throw new IllegalArgumentException("Book requires valid price");
            }
        }


        if (contentValues.containsKey(BookContract.BookEntry.COLUMN_PRODUCT_QUANTINTY)) {

            Integer quantity = contentValues.getAsInteger(BookContract.BookEntry.COLUMN_PRODUCT_QUANTINTY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Book requires valid quantity");
            }
        }

        if (contentValues.containsKey(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME)) {
            String sup_name = contentValues.getAsString(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            if (sup_name == null) {
                throw new IllegalArgumentException("Book requires a supplier name");
            }
        }

        if (contentValues.containsKey(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME)) {
            String sup_phone = contentValues.getAsString(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            if (sup_phone == null) {
                throw new IllegalArgumentException("Book requires a supplier phone");
            }
        }


        if (contentValues.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(BookContract.BookEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }
}
