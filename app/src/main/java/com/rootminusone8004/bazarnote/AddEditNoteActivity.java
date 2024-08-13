package com.rootminusone8004.bazarnote;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class AddEditNoteActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "com.rootminusone8004.bazarnote.EXTRA_ID";
    public static final String EXTRA_PRICE_CHECK = "com.rootminusone8004.bazarnote.EXTRA_PRICE_CHECK";
    public static final String EXTRA_TITLE = "com.rootminusone8004.bazarnote.EXTRA_TITLE";
    public static final String EXTRA_QUANTITY = "com.rootminusone8004.bazarnote.EXTRA_QUANTITY";
    public static final String EXTRA_PRICE = "com.rootminusone8004.bazarnote.EXTRA_PRICE";

    private EditText editTextItem;;
    private EditText editTextQuantity;
    private EditText editTextPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_add_note);

        editTextItem = findViewById(R.id.edit_text_item);
        editTextQuantity = findViewById(R.id.edit_text_quantity);
        editTextPrice = findViewById(R.id.edit_text_price);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            if (intent.hasExtra(EXTRA_PRICE_CHECK)) {
                setTitle(R.string.header_add_price);
                editTextItem.setVisibility(View.GONE);
                editTextQuantity.setVisibility(View.GONE);
            } else {
                setTitle(R.string.header_edit_note);
                editTextItem.setText(intent.getStringExtra(EXTRA_TITLE));
                editTextQuantity.setText(Float.toString(intent.getFloatExtra(EXTRA_QUANTITY, 0.0f)));
                editTextPrice.setVisibility(View.GONE);
            }
        } else {
            setTitle(R.string.header_add_note);
            editTextPrice.setVisibility(View.GONE);
        }
    }

    private void saveNote(boolean updateFlag) {
        String item = editTextItem.getText().toString();
        String quantityInString = editTextQuantity.getText().toString();
        int price;

        if (item.trim().isEmpty() || quantityInString.trim().isEmpty()) {
            Toast.makeText(this, R.string.toast_unfilled_note, Toast.LENGTH_SHORT).show();
            return;
        }
        
        float quantity = Float.parseFloat(quantityInString);

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, item);
        data.putExtra(EXTRA_QUANTITY, quantity);

        if(updateFlag) {
            price = getIntent().getIntExtra(EXTRA_PRICE, -1);
        } else {
            price = 0;
        }
        data.putExtra(EXTRA_PRICE, price);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();
    }



    private void savePrice(String item, float quantity) {
        String priceInString = editTextPrice.getText().toString();

        if (priceInString.trim().isEmpty()) {
            Toast.makeText(this, R.string.toast_field_fill_indicate, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int price = Integer.parseInt(priceInString);

            Intent data = new Intent();
            data.putExtra(EXTRA_TITLE, item);
            data.putExtra(EXTRA_QUANTITY, quantity);
            data.putExtra(EXTRA_PRICE, price);

            int id = getIntent().getIntExtra(EXTRA_ID, -1);
            if (id != -1) {
                data.putExtra(EXTRA_ID, id);
            }

            setResult(RESULT_OK, data);
            finish();
        } catch(Exception e) {
            Toast.makeText(this, R.string.toast_note_fill_int_indicate, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.save_note) {
            Intent look = getIntent();
            if (look.hasExtra(EXTRA_ID)) {
                if (look.hasExtra(EXTRA_PRICE_CHECK)) {
                    savePrice(look.getStringExtra(EXTRA_TITLE), look.getFloatExtra(AddEditNoteActivity.EXTRA_QUANTITY, 0));
                    return true;
                } else {
                    saveNote(true);
                    return true;
                }
            } else {
                saveNote(false);
                return true;
            }
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}