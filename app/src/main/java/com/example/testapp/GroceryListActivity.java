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

public class GroceryListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView groceryRecyclerView;
    private GroceryAdapter adapter;
    private List<GroceryItem> groceryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);

        dbHelper = new DatabaseHelper(this);
        groceryRecyclerView = findViewById(R.id.groceryRecyclerView);
        groceryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groceryList = new ArrayList<>();
        adapter = new GroceryAdapter(this, groceryList, dbHelper);
        groceryRecyclerView.setAdapter(adapter);

        loadGroceryItems();

        Button addGroceryItemButton = findViewById(R.id.addGroceryItemButton);
        addGroceryItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddGroceryItemDialog();
            }
        });

        Button moveToPantryButton = findViewById(R.id.moveToPantryButton);
        moveToPantryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveGroceryItemsToPantry();
            }
        });
    }


    private void loadGroceryItems() {
        Cursor cursor = dbHelper.getAllGroceryItems();
        groceryList.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                groceryList.add(new GroceryItem(id, name, quantity));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void showAddGroceryItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Grocery Item");

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

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String itemName = itemNameInput.getText().toString().trim();
                int itemQuantity = Integer.parseInt(itemQuantityInput.getText().toString().trim());

                // Check if the item already exists in the grocery list
                GroceryItem existingGroceryItem = dbHelper.getGroceryItemByName(itemName);

                if (existingGroceryItem != null) {
                    // Item exists, update its quantity
                    int newQuantity = existingGroceryItem.getQuantity() + itemQuantity;
                    dbHelper.updateGroceryItemQuantity(existingGroceryItem.getId(), newQuantity);
                } else {
                    // Item does not exist, insert it as a new item
                    dbHelper.insertGroceryItem(itemName, itemQuantity);
                }

                // Refresh the grocery list
                loadGroceryItems();
                Toast.makeText(GroceryListActivity.this, "Item added or updated", Toast.LENGTH_SHORT).show();
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

    private void moveGroceryItemsToPantry() {
        for (GroceryItem groceryItem : groceryList) {
            String itemName = groceryItem.getName();
            int groceryQuantity = groceryItem.getQuantity();

            // Check if the item already exists in the pantry
            PantryItem pantryItem = dbHelper.getPantryItemByName(itemName);

            if (pantryItem != null) {
                // Item exists in the pantry, update its quantity
                int newQuantity = pantryItem.getQuantity() + groceryQuantity;
                dbHelper.updatePantryItemQuantity(pantryItem.getId(), newQuantity);
            } else {
                // Item does not exist, add it to the pantry
                dbHelper.insertPantryItem(itemName, groceryQuantity);
            }

            // Remove the item from the grocery list
            dbHelper.deleteGroceryItem(groceryItem.getId());
        }

        // Clear the grocery list and refresh the view
        groceryList.clear();
        adapter.notifyDataSetChanged();

        Toast.makeText(this, "Items moved to pantry", Toast.LENGTH_SHORT).show();
    }

}
