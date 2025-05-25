package com.example.pantrybuddy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> recipes;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }

    public RecipeAdapter(List<Recipe> recipes, OnItemClickListener onItemClickListener) {
        this.recipes = recipes;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.titleTextView.setText(recipe.getLabel());
        Glide.with(holder.itemView.getContext())
                .load(recipe.getImage())
                .into(holder.recipeImageView);

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged(); // Notify the RecyclerView that the data has changed
    }

    // Add results of API call
    public void addRecipes(List<Recipe> newRecipes) {
        int startPosition = recipes.size();
        recipes.addAll(newRecipes);
        notifyItemRangeInserted(startPosition, newRecipes.size());
    }


    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView recipeImageView;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImageView = itemView.findViewById(R.id.recipeImage);
            titleTextView = itemView.findViewById(R.id.recipeName);
        }
    }
}
