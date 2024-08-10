package com.example.testapp;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PantryActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView pantryRecyclerView;
    private PantryAdapter adapter;
    private List<PantryItem> pantryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        dbHelper = new DatabaseHelper(this);
        pantryRecyclerView = findViewById(R.id.pantryRecyclerView);

        pantryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pantryList = new ArrayList<>();
        adapter = new PantryAdapter(this, pantryList, dbHelper); // Pass dbHelper here
        pantryRecyclerView.setAdapter(adapter);

        loadPantryItems();

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
                pantryList.add(new PantryItem(id, name, quantity));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Pantry Item");

        final EditText itemNameInput = new EditText(this);
        itemNameInput.setHint("Item Name");

        final EditText itemQuantityInput = new EditText(this);
        itemQuantityInput.setHint("Quantity");
        itemQuantityInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(itemNameInput);
        layout.addView(itemQuantityInput);

        builder.setView(layout);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String itemName = itemNameInput.getText().toString();
                String itemQuantityStr = itemQuantityInput.getText().toString();
                if (!itemName.isEmpty() && !itemQuantityStr.isEmpty()) {
                    int itemQuantity = Integer.parseInt(itemQuantityStr);
                    dbHelper.insertPantryItem(itemName, itemQuantity);
                    loadPantryItems(); // Refresh the list
                } else {
                    Toast.makeText(PantryActivity.this, "Please enter both name and quantity", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    private void showAddPantryItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Pantry Item");

        // Set up the input fields
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText itemNameInput = new EditText(this);
        itemNameInput.setHint("Item Name");
        layout.addView(itemNameInput);

        final EditText itemQuantityInput = new EditText(this);
        itemQuantityInput.setHint("Quantity");
        itemQuantityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(itemQuantityInput);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String itemName = itemNameInput.getText().toString().trim();
                int itemQuantity = Integer.parseInt(itemQuantityInput.getText().toString().trim());

                // Check if the item already exists in the pantry
                PantryItem existingPantryItem = dbHelper.getPantryItemByName(itemName);

                if (existingPantryItem != null) {
                    // Item exists, update its quantity
                    int newQuantity = existingPantryItem.getQuantity() + itemQuantity;
                    dbHelper.updatePantryItemQuantity(existingPantryItem.getId(), newQuantity);
                } else {
                    // Item does not exist, insert it as a new item
                    dbHelper.insertPantryItem(itemName, itemQuantity);
                }

                // Refresh the pantry list
                loadPantryItems();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}
