package com.example.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button pantryButton = findViewById(R.id.pantryButton);
        Button groceryListButton = findViewById(R.id.groceryListButton);
        Button recipeButton = findViewById(R.id.recipeButton);

        pantryButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PantryActivity.class);
            startActivity(intent);
        });

        groceryListButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GroceryListActivity.class);
            startActivity(intent);
        });

        recipeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecipeActivity.class);
            startActivity(intent);
        });
    }
}
