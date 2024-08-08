package com.example.testapp;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PantryActivity extends AppCompatActivity {
    private RecyclerView pantryRecyclerView;
    private PantryAdapter pantryAdapter;
    private List<PantryItem> pantryItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        pantryItems = getPantryItems(); // Fetch pantry items from database

        pantryRecyclerView = findViewById(R.id.pantryRecyclerView);
        pantryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        pantryAdapter = new PantryAdapter(pantryItems);
        pantryRecyclerView.setAdapter(pantryAdapter);

        Button addItemButton = findViewById(R.id.addItemButton);
        addItemButton.setOnClickListener(v -> {
            // Code to add item to pantry and update database
            // e.g., show a dialog to input new item details, add to list, and notify adapter
        });
    }

    private List<PantryItem> getPantryItems() {
        // Fetch pantry items from database
        // Placeholder for demonstration
        List<PantryItem> items = new ArrayList<>();
        items.add(new PantryItem("Apples", 5));
        items.add(new PantryItem("Bananas", 7));
        return items;
    }
}

