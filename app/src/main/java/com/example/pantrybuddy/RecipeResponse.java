package com.example.pantrybuddy;

import java.util.List;

public class RecipeResponse {
    private List<Hit> hits;

    public RecipeResponse(List<Hit> hits) {
        this.hits = hits;
    }

    public List<Hit> getHits() {
        return hits;
    }
}

class Hit {
    private Recipe recipe;

    public Recipe getRecipe() {
        return recipe;
    }
}