package com.tatsujin.recipe.requests;

import com.tatsujin.recipe.requests.responses.RecipeResponse;
import com.tatsujin.recipe.requests.responses.RecipeSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApi {


    // search
    @GET("api/search")
    Call<RecipeSearchResponse> searchRecipe(@Query("key") String key , @Query("q") String query , @Query("page") String page) ;


    // Get Recipe
    @GET("api/get")
    Call<RecipeResponse> getRecipe(@Query("key") String key , @Query("rId") String Recipe_id);



}
