package com.example.android.bookstoreapp;


import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;
import com.example.android.bookstoreapp.data.BookDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG ="queryData result is : ";
    private BookDbHelper mDbHelper;

    /** Identifier for the pet data loader */
    private static final int BOOK_LOADER = 0;

    /** Adapter for the ListView */
    BookCursorAdapter mCursorAdapter;

    ListView bookListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button insert_btn =  (Button) findViewById(R.id.new_book);
        insert_btn.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this,InsertActivity.class);
                    startActivity(intent);
                }
      });

//
//        FloatingActionButton fab =(FloatingActionButton) findViewById(R.id.new_book);
//        fab.setImageResource(R.drawable.book_icon);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this,InsertActivity.class);
//                startActivity(intent);
//            }
//        });




        mDbHelper = new BookDbHelper(this);


        // Find the ListView which will be populated with the pet data
         bookListView = (ListView) findViewById(R.id.list);


        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of book data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);



        getSupportLoaderManager().initLoader(BOOK_LOADER, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {

          case R.id.action_delete_all_entries:
              showDeleteConfirmationDialog();
              return true;
      }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {

        // Create an AlertDialog.Builder and set the appropriate message and listeners
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_everything);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteAllPets();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                // dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRODUCT_PRICE,
                BookEntry.COLUMN_PRODUCT_QUANTINTY
                    };

        // This loader will execute the ContentProvider's query method on a background thread
       return new CursorLoader(this, BookEntry.CONTENT_URI, projection,null, null,  null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    public void clickOnViewItem(long id) {
        Intent intent = new Intent(this, DetailsActivity.class);
        Uri currentPetUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);

        // Set the URI on the data field of the intent
        intent.setData(currentPetUri);
        startActivity(intent);
    }

    public void clickOnSale(long id, int quantity) {
        mDbHelper.sellBook(id, quantity);
        mCursorAdapter.swapCursor(mDbHelper.queryData());
    }
}
