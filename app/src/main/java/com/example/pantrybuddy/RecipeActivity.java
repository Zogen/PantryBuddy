package com.example.pantrybuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class RecipeActivity extends AppCompatActivity {

    private static final String APP_ID = BuildConfig.EDAMAM_APP_ID;
    private static final String APP_KEY = BuildConfig.EDAMAM_APP_KEY;
    private static final String RECIPES_CACHE_KEY = "recipes_cache_key";
    private static final String PANTRY_UPDATED_KEY = "pantry_updated_key";

    private RecyclerView recipeRecyclerView;
    private RecipeAdapter adapter;
    private List<Recipe> recipeList;
    private Recipe currentRecipe;

    private SharedPreferences sharedPreferences;
    private boolean pantryUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // Display action bar with up button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Recipes");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        pantryUpdated = sharedPreferences.getBoolean(PANTRY_UPDATED_KEY, false);

        recipeRecyclerView = findViewById(R.id.recipeRecyclerView);
        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RecipeAdapter(new ArrayList<>(), this::showRecipeDetails);
        recipeRecyclerView.setAdapter(adapter);

        // Restore saved instance state or fetch recipes
        if (savedInstanceState != null) {
            List<Recipe> savedRecipes = savedInstanceState.getParcelableArrayList("recipes");
            if (savedRecipes != null) {
                adapter.setRecipes(savedRecipes);
            }
            currentRecipe = savedInstanceState.getParcelable("currentRecipe");
            if (currentRecipe != null) {
                showRecipeDetails(currentRecipe);
            }
        } else {
            // Check for cache and pantry updates
            fetchRecipesBasedOnPantry();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the list of recipes to the bundle
        outState.putParcelableArrayList("recipes", new ArrayList<>(adapter.getRecipes()));

        if (currentRecipe != null) {
            outState.putParcelable("currentRecipe", currentRecipe);
        }
    }


    private void fetchRecipes(String query) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.edamam.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RecipeApiService apiService = retrofit.create(RecipeApiService.class);
        Call<RecipeResponse> call = apiService.getRecipes(query, APP_ID, APP_KEY, 0, 20);

        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Hit> hits = response.body().getHits();
                    List<Recipe> newRecipes = new ArrayList<>();
                    for (Hit hit : hits) {
                        newRecipes.add(hit.getRecipe());
                    }
                    adapter.addRecipes(newRecipes);

                    // Cache the new recipes
                    cacheRecipes(newRecipes);

                    // Mark pantry as not updated
                    sharedPreferences.edit().putBoolean(PANTRY_UPDATED_KEY, false).apply();
                } else {
                    Toast.makeText(RecipeActivity.this, "Failed to load recipes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                Log.e("RecipeActivity", "Error fetching recipes", t);
                Toast.makeText(RecipeActivity.this, "Error fetching recipes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRecipeDetails(Recipe recipe) {
        currentRecipe = recipe;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the dialog_recipe_details.xml layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_recipe_details, null);
        builder.setView(dialogView);

        // Set the recipe details in the dialog
        TextView recipeName = dialogView.findViewById(R.id.recipeDetailName);
        TextView recipeIngredients = dialogView.findViewById(R.id.recipeDetailIngredients);
        TextView tvRecipeUrl = dialogView.findViewById(R.id.recipeUrl);

        tvRecipeUrl.setText(recipe.getUrl());
        tvRecipeUrl.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(recipe.getUrl()));
            startActivity(intent);
        });

        recipeName.setText(recipe.getLabel());

        StringBuilder ingredientsBuilder = new StringBuilder();
        for (String ingredient : recipe.getIngredientLines()) {
            ingredientsBuilder.append("- ").append(ingredient).append("\n");
        }
        recipeIngredients.setText(ingredientsBuilder.toString());

        builder.setPositiveButton("Close", (dialog, which) -> {
            currentRecipe = null;
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void fetchRecipesBasedOnPantry() {
        // Step 1: Check if cache is available and pantry has not been updated
        String cachedRecipes = sharedPreferences.getString(RECIPES_CACHE_KEY, null);

        if (!pantryUpdated && cachedRecipes != null) {
            // Load cached recipes
            recipeList = parseRecipesFromCache(cachedRecipes);
            adapter.setRecipes(recipeList);
        } else {
            // Step 2: Retrieve pantry items and fetch recipes
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            List<String> pantryItems = dbHelper.getPantryItems();
            String queryString = buildQueryString(pantryItems);

            // Fetch recipes using API and cache them
            fetchRecipes(queryString);
        }
    }

    // Make the query string from the list of ingredients, to use in API call
    private String buildQueryString(List<String> ingredients) {
        return String.join(" ", ingredients);
    }

    private List<Recipe> parseRecipesFromCache(String cachedRecipes) {
        Gson gson = new Gson();
        Type recipeListType = new TypeToken<ArrayList<Recipe>>() {}.getType();
        return gson.fromJson(cachedRecipes, recipeListType);
    }

    private void cacheRecipes(List<Recipe> recipes) {
        Gson gson = new Gson();
        String recipesJson = gson.toJson(recipes);
        sharedPreferences.edit().putString(RECIPES_CACHE_KEY, recipesJson).apply();
    }
}
