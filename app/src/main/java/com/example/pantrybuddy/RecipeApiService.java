package com.example.pantrybuddy;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApiService {
    @GET("search")
    Call<RecipeResponse> getRecipes(
            @Query("q") String query,
            @Query("app_id") String appId,
            @Query("app_key") String appKey,
            @Query("from") int from,
            @Query("to") int to
    );
}
