package com.example.android.bookstoreapp;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;

/**
 * Created by giorgosnty on 4/6/2018.
 */

public class InsertActivity extends AppCompatActivity {

    private static final String TAG ="can you find me?" ;
    private EditText titleEditText;
    private EditText priceEditText;
    private EditText quantityEditText;
    private EditText supplierNameEditText;
    private EditText supplierTelephoneEditText;

   private static boolean somethingNull = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_insert);



        titleEditText = (EditText) findViewById(R.id.edit_book_name);
        priceEditText = (EditText) findViewById(R.id.edit_book_price);
        quantityEditText = (EditText) findViewById(R.id.book_quantity);
        supplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        supplierTelephoneEditText = (EditText) findViewById(R.id.edit_supplier_number);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem menuItem = menu.findItem(R.id.action_delete);
        menuItem.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.action_save) {
            saveBook();
            if (somethingNull) {
                somethingNull = false;
                return false;
            }
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
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
        String supplier_name = supplierNameEditText.getText().toString().trim();
        String supplier_phone =supplierTelephoneEditText.getText().toString().trim();



        ContentValues values = new ContentValues();

        values.put(BookEntry.COLUMN_PRODUCT_NAME,title);
        values.put(BookEntry.COLUMN_PRODUCT_PRICE,price);
        values.put(BookEntry.COLUMN_PRODUCT_QUANTINTY,quantity);
        values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME,supplier_name);
        values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE,supplier_phone);

        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI,values);

        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.insert_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.insert_succeeded),
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void isSomethingNull(){
        if(isEditTextNull(titleEditText,"title")){
            somethingNull = true;
        }else if(isEditTextNull(priceEditText,"price")){
            somethingNull =  true;
        }else if(isEditTextNull(quantityEditText,"quantity")){
            somethingNull = true;
        }else if(isEditTextNull(supplierNameEditText,"Supplier's name")){
            somethingNull = true;
        }else if(isEditTextNull(supplierTelephoneEditText,"Supplier's phone")){
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
