package com.rootminusone8004.bazarnote.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.rootminusone8004.bazarnote.R;
import com.rootminusone8004.bazarnote.data.Note;
import com.rootminusone8004.bazarnote.databinding.ActivityAddNoteBinding;
import com.rootminusone8004.bazarnote.guide.TapAddEditNoteActivity;

/**
 * Screen used to add a note, edit a note, or fill in the price of an existing note.
 * Use the {@code new*Intent} factories to launch it and {@link #noteFromResult(Intent)} to read the result.
 */
public class AddEditNoteActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "com.rootminusone8004.bazarnote.EXTRA_ID";
    public static final String EXTRA_PRICE_CHECK = "com.rootminusone8004.bazarnote.EXTRA_PRICE_CHECK";
    public static final String EXTRA_TITLE = "com.rootminusone8004.bazarnote.EXTRA_TITLE";
    public static final String EXTRA_QUANTITY = "com.rootminusone8004.bazarnote.EXTRA_QUANTITY";
    public static final String EXTRA_PRICE = "com.rootminusone8004.bazarnote.EXTRA_PRICE";
    public static final String EXTRA_INCLUDED = "com.rootminusone8004.bazarnote.EXTRA_INCLUDED";

    private enum Mode {ADD, EDIT, ADD_PRICE}

    private ActivityAddNoteBinding binding;
    private Mode mode;

    public static Intent newAddIntent(Context context) {
        return new Intent(context, AddEditNoteActivity.class);
    }

    public static Intent newEditIntent(Context context, Note note) {
        return newAddIntent(context)
                .putExtra(EXTRA_ID, note.getId())
                .putExtra(EXTRA_TITLE, note.getItem())
                .putExtra(EXTRA_QUANTITY, note.getQuantity())
                .putExtra(EXTRA_PRICE, note.getPrice())
                .putExtra(EXTRA_INCLUDED, note.isIncluded());
    }

    public static Intent newAddPriceIntent(Context context, Note note) {
        return newAddIntent(context)
                .putExtra(EXTRA_ID, note.getId())
                .putExtra(EXTRA_TITLE, note.getItem())
                .putExtra(EXTRA_QUANTITY, note.getQuantity())
                .putExtra(EXTRA_PRICE_CHECK, 5)
                .putExtra(EXTRA_INCLUDED, note.isIncluded());
    }

    /** Builds the note described by a result of this screen. Newly added notes keep their default id (0). */
    public static Note noteFromResult(Intent data) {
        Note note = new Note(
                data.getStringExtra(EXTRA_TITLE),
                data.getFloatExtra(EXTRA_QUANTITY, 0),
                data.getFloatExtra(EXTRA_PRICE, 0));
        if (data.hasExtra(EXTRA_ID)) {
            note.setId(data.getIntExtra(EXTRA_ID, 0));
        }
        note.setIncluded(data.getBooleanExtra(EXTRA_INCLUDED, true));
        return note;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        }

        Intent intent = getIntent();
        if (!intent.hasExtra(EXTRA_ID)) {
            mode = Mode.ADD;
            setTitle(R.string.header_add_note);
            binding.editTextPrice.setVisibility(View.GONE);
        } else if (intent.hasExtra(EXTRA_PRICE_CHECK)) {
            mode = Mode.ADD_PRICE;
            setTitle(R.string.header_add_price);
            binding.editTextItem.setVisibility(View.GONE);
            binding.editTextQuantity.setVisibility(View.GONE);
            if (binding.editTextPrice.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.editTextPrice.getLayoutParams();
                params.topMargin = 0;
                binding.editTextPrice.setLayoutParams(params);
            }
        } else {
            mode = Mode.EDIT;
            setTitle(R.string.header_edit_note);
            binding.editTextItem.setText(intent.getStringExtra(EXTRA_TITLE));
            binding.editTextQuantity.setText(Float.toString(intent.getFloatExtra(EXTRA_QUANTITY, 0.0f)));
            float price = intent.getFloatExtra(EXTRA_PRICE, 0.0f);
            if (price > 0) {
                binding.editTextPrice.setText(Float.toString(price));
            }
            binding.editTextPrice.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_note_menu, menu);

        // Wait for the toolbar to lay out so the save action can be highlighted.
        new Handler(Looper.getMainLooper()).post(() -> {
            if (findViewById(R.id.save_note) == null) {
                return;
            }
            TapAddEditNoteActivity guide = new TapAddEditNoteActivity(this);
            if (mode == Mode.ADD_PRICE) {
                guide.startGuideInPrice();
            } else {
                guide.startGuide();
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_note) {
            if (mode == Mode.ADD_PRICE) {
                savePrice();
            } else {
                saveNote();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNote() {
        String item = binding.editTextItem.getText().toString();
        String quantityText = binding.editTextQuantity.getText().toString();

        if (item.trim().isEmpty() || quantityText.trim().isEmpty()) {
            Toast.makeText(this, R.string.toast_unfilled_note, Toast.LENGTH_SHORT).show();
            return;
        }

        float quantity;
        try {
            quantity = Float.parseFloat(quantityText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.toast_note_fill_int_indicate, Toast.LENGTH_SHORT).show();
            return;
        }

        float price = 0.0f;
        if (mode == Mode.EDIT) {
            String priceText = binding.editTextPrice.getText().toString().trim();
            if (!priceText.isEmpty()) {
                try {
                    price = Float.parseFloat(priceText);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, R.string.toast_note_fill_int_indicate, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        finishWithResult(item, quantity, price);
    }

    private void savePrice() {
        String priceText = binding.editTextPrice.getText().toString();

        if (priceText.trim().isEmpty()) {
            Toast.makeText(this, R.string.toast_field_fill_indicate, Toast.LENGTH_SHORT).show();
            return;
        }

        float price;
        try {
            price = Float.parseFloat(priceText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.toast_note_fill_int_indicate, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent source = getIntent();
        finishWithResult(source.getStringExtra(EXTRA_TITLE), source.getFloatExtra(EXTRA_QUANTITY, 0), price);
    }

    private void finishWithResult(String item, float quantity, float price) {
        boolean isIncluded = getIntent().getBooleanExtra(EXTRA_INCLUDED, true);
        Intent data = new Intent()
                .putExtra(EXTRA_TITLE, item)
                .putExtra(EXTRA_QUANTITY, quantity)
                .putExtra(EXTRA_PRICE, price)
                .putExtra(EXTRA_INCLUDED, isIncluded);

        if (getIntent().hasExtra(EXTRA_ID)) {
            data.putExtra(EXTRA_ID, getIntent().getIntExtra(EXTRA_ID, 0));
        }

        setResult(RESULT_OK, data);
        finish();
    }
}
