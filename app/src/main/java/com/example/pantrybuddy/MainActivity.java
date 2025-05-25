package com.example.pantrybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //display action bar, no up-button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Home");
        }

        //set up buttons and listeners
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
