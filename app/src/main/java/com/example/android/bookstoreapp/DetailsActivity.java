package com.example.android.bookstoreapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract;


/**
 * Created by giorgosnty on 29/6/2018.
 */

//this activiti will be used for both inspectiong the details of the products
//and editing them
public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;
    private static boolean somethingNull = false;

    private Uri currentBookUri;
    private EditText titleEditText;
    private EditText priceEditText;
    private EditText quantityEditText;
    private EditText telephoneSupEditText;
    private EditText nameSupEditText;
    private boolean bookHasChanged = false;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        currentBookUri = intent.getData();

        titleEditText = (EditText) findViewById(R.id.edit_book_name_detail);
        priceEditText = (EditText) findViewById(R.id.edit_book_price_detail);
        quantityEditText = (EditText) findViewById(R.id.book_quantity_detail);
        nameSupEditText = (EditText) findViewById(R.id.edit_book_supplier_name_detail);
        telephoneSupEditText = (EditText) findViewById(R.id.edit_supplier_phone_detail);

        Button increase_btn = (Button) findViewById(R.id.increase_quantity_btn);

        increase_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseQuantityByOne();
            }
        });

        Button decrease_btn = (Button) findViewById(R.id.decrease_quantity_btn);

        decrease_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseQuantityByOne();
            }
        });

        Button contact_btn = (Button) findViewById(R.id.contact_btn);

        contact_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactSupplier();
            }
        });


        getSupportLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);


    }

    private void contactSupplier() {

        String supplierTelephone = telephoneSupEditText.getText().toString();

        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", supplierTelephone, null));
        startActivity(intent);
    }

    private void decreaseQuantityByOne() {
        String prevValue = quantityEditText.getText().toString();

        int previousIntValue = 0;

        if(!(prevValue.isEmpty()||prevValue.equals("0"))){
            previousIntValue = Integer.parseInt(prevValue);
            quantityEditText.setText(String.valueOf(previousIntValue-1));
        }

    }

    private void increaseQuantityByOne() {
        String prevValue = quantityEditText.getText().toString();

        int previousIntValue = 0;

        if(!prevValue.isEmpty()){
            previousIntValue = Integer.parseInt(prevValue);

        }
        quantityEditText.setText(String.valueOf(previousIntValue + 1));
    }

    //Look for touch on the screen,which is implying modification
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
          bookHasChanged = true;
            return false;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (currentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }




    private void saveBook(){

        //check if anything is null and if it is make the boolean static variable truw
        isSomethingNull();


        if(somethingNull){
            return;

        }

        String title = titleEditText.getText().toString().trim();
        int price = Integer.parseInt(priceEditText.getText().toString().trim());
        int quantity = Integer.parseInt(quantityEditText.getText().toString().trim());
        String supplier_name = nameSupEditText.getText().toString().trim();
        String supplier_phone =telephoneSupEditText.getText().toString().trim();



        ContentValues values = new ContentValues();

        values.put(BookContract.BookEntry.COLUMN_PRODUCT_NAME,title);
        values.put(BookContract.BookEntry.COLUMN_PRODUCT_PRICE,price);
        values.put(BookContract.BookEntry.COLUMN_PRODUCT_QUANTINTY,quantity);
        values.put(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME,supplier_name);
        values.put(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE,supplier_phone);


            // Otherwise this is an existing book
            int rowsAffected = getContentResolver().update(currentBookUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.update_succeeded),
                        Toast.LENGTH_SHORT).show();
            }



    }




    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue
        if (!bookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Save is pressed
            case R.id.action_save:

                saveBook();
                //if something is null dont finish the activity
                //just make the boolean false ang start over
                if(somethingNull){
                    somethingNull = false;
                    return true;
                }
                finish();
                return true;
            // Delete is pressed
            case R.id.action_delete:

                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                if (!bookHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                    return true;
                }



                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            }
                        };


                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_PRODUCT_NAME,
                BookContract.BookEntry.COLUMN_PRODUCT_PRICE,
                BookContract.BookEntry.COLUMN_PRODUCT_QUANTINTY,
                BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                currentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        //no need for further analysis,so return
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        if (cursor.moveToFirst()) {

            // Find the columns of pet attributes that we're interested in
            int titleColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_QUANTINTY);
            int supNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supPhoneColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(titleColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supName = cursor.getString(supNameColumnIndex);
            String supPhone = cursor.getString(supPhoneColumnIndex);

            // Update the views on the screen with the values from the database
            titleEditText.setText(title);
            priceEditText.setText(price);
            quantityEditText.setText(Integer.toString(quantity));
            nameSupEditText.setText(supName);
            telephoneSupEditText.setText(supPhone);



        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        titleEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        nameSupEditText.setText("");
        telephoneSupEditText.setText("");
    }

    private void showDeleteConfirmationDialog() {

        // Create an AlertDialog.Builder and set the appropriate message and listeners
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
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

    private void deletePet() {

        if (currentBookUri != null) {

            int rowsDeleted = getContentResolver().delete(currentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.delete_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.delete_sucess),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }


    private void isSomethingNull(){
        if(isEditTextNull(titleEditText,"title")){
            somethingNull = true;
        }else if(isEditTextNull(priceEditText,"price")){
            somethingNull =  true;
        }else if(isEditTextNull(quantityEditText,"quantity")){
            somethingNull = true;
        }else if(isEditTextNull(nameSupEditText,"Supplier's name")){
            somethingNull = true;
        }else if(isEditTextNull(telephoneSupEditText,"Supplier's phone")){
            somethingNull = true;
        }
    }

    private boolean isEditTextNull(EditText editText, String description) {
        if(TextUtils.isEmpty(editText.getText())){
            Toast.makeText(this, "Missing field "+description,
                    Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

}
