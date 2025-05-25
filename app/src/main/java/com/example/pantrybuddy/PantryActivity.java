package com.example.pantrybuddy;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PantryActivity extends AppCompatActivity implements PantryAdapter.OnPantryItemChangeListener {

    private static final String PANTRY_UPDATED_KEY = "pantry_updated_key"; // Key for SharedPreferences
    private static final String SHARED_PREFS_NAME = "MyAppPreferences"; // SharedPreferences name
    private DatabaseHelper dbHelper;
    private RecyclerView pantryRecyclerView;
    private PantryAdapter adapter;
    private List<PantryItem> pantryList;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        // display action bar with up button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Pantry"); // Set the title to the name of the activity
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable the Up button
        }

        //initializing activity components
        sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE); // Initialize SharedPreferences
        dbHelper = new DatabaseHelper(this);
        pantryRecyclerView = findViewById(R.id.pantryRecyclerView);

        pantryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pantryList = new ArrayList<>();
        adapter = new PantryAdapter(this, pantryList, dbHelper, this); // Pass 'this' as the listener
        pantryRecyclerView.setAdapter(adapter);

        //method to load items registered in Pantry db table, implemented below
        loadPantryItems();

        //button to add new item to pantry
        Button addPantryItemButton = findViewById(R.id.addPantryItemButton);
        addPantryItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddPantryItemDialog();
            }
        });
    }

    private void loadPantryItems() {
        Cursor cursor = dbHelper.getAllPantryItems();
        pantryList.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
                String date = cursor.getString(cursor.getColumnIndex("expiration_date"));
                pantryList.add(new PantryItem(id, name, quantity, date));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    //dialog for adding new item to pantry set. initiated by button press
    private void showAddPantryItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Pantry Item");

        // Set up the input fields
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //item label
        final EditText itemNameInput = new EditText(this);
        itemNameInput.setHint("Item Name");
        layout.addView(itemNameInput);

        //item quantity
        final EditText itemQuantityInput = new EditText(this);
        itemQuantityInput.setHint("Quantity");
        itemQuantityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(itemQuantityInput);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String itemName = itemNameInput.getText().toString();
                String itemQuantityStr = itemQuantityInput.getText().toString();

                // check that both fields have been filled, and that quantity is > 0
                if (!itemName.isEmpty() && !itemQuantityStr.isEmpty() && Integer.parseInt(itemQuantityStr) > 0) {
                    int itemQuantity = Integer.parseInt(itemQuantityStr);
                    PantryItem existingPantryItem = dbHelper.getPantryItemByName(itemName);
                    if (existingPantryItem != null) {
                        // Item exists, update its quantity
                        int newQuantity = existingPantryItem.getQuantity() + itemQuantity;
                        existingPantryItem.setQuantity(newQuantity);
                        dbHelper.updatePantryItemQuantity(existingPantryItem.getId(), newQuantity);
                    } else {
                        // Item does not exist, insert it as a new item
                        dbHelper.insertPantryItem(itemName, itemQuantity, null);

                        // Mark that the pantry has been updated, this results in new API call in recipes activity
                        setPantryUpdatedFlag();
                    }

                    // Update successful, notify user
                    Toast.makeText(PantryActivity.this, "Item added or updated", Toast.LENGTH_SHORT).show();
                    loadPantryItems();
                } else {
                    // Error, notify user to correct their input
                    Toast.makeText(PantryActivity.this, "Please enter valid name and quantity", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Method to set the pantry updated flag in SharedPreferences
    private void setPantryUpdatedFlag() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PANTRY_UPDATED_KEY, true); // Set the flag to true
        editor.apply();
    }

    public void onPantryItemRemoved() {
        setPantryUpdatedFlag();
    }

}
