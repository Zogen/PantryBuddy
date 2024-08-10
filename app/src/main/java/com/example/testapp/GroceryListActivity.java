package com.example.testapp;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
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
        adapter = new GroceryAdapter(this, groceryList, dbHelper); // Pass dbHelper here
        groceryRecyclerView.setAdapter(adapter);

        loadGroceryItems();

        findViewById(R.id.addGroceryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddItemDialog();
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
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
                groceryList.add(new GroceryItem(id, name, quantity));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Grocery Item");

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
                    dbHelper.insertGroceryItem(itemName, itemQuantity);
                    loadGroceryItems(); // Refresh the list
                } else {
                    Toast.makeText(GroceryListActivity.this, "Please enter both name and quantity", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

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
