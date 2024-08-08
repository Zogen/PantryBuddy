package com.example.testapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class GroceryListActivity extends AppCompatActivity {

    private RecyclerView groceryRecyclerView;
    private GroceryAdapter groceryAdapter;
    private List<GroceryItem> groceryList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);

        groceryRecyclerView = findViewById(R.id.groceryRecyclerView);
        groceryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        groceryList = new ArrayList<>();
        groceryAdapter = new GroceryAdapter(groceryList);
        groceryRecyclerView.setAdapter(groceryAdapter);

        findViewById(R.id.addGroceryButton).setOnClickListener(v -> showAddItemDialog());
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
                    groceryList.add(new GroceryItem(itemName, itemQuantity));
                    groceryAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(GroceryListActivity.this, "Please enter both name and quantity", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }
}
