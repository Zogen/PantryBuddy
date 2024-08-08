package com.example.testapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {
    private RecyclerView recipeRecyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        recipes = getSuggestedRecipes(); // Fetch suggested recipes based on pantry items

        recipeRecyclerView = findViewById(R.id.recipeRecyclerView);
        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        recipeAdapter = new RecipeAdapter(recipes);
        recipeRecyclerView.setAdapter(recipeAdapter);
    }

    private List<Recipe> getSuggestedRecipes() {
        // Fetch suggested recipes based on pantry items
        // Placeholder for demonstration
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new Recipe("Apple Pie", "A delicious apple pie recipe."));
        recipes.add(new Recipe("Banana Smoothie", "A healthy banana smoothie recipe."));
        return recipes;
    }
}

