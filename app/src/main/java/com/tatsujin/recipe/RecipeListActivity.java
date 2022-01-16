package com.tatsujin.recipe;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.tatsujin.recipe.models.Recipe;
import com.tatsujin.recipe.requests.RecipeApi;
import com.tatsujin.recipe.requests.ServiceGenerator;
import com.tatsujin.recipe.requests.responses.RecipeResponse;
import com.tatsujin.recipe.requests.responses.RecipeSearchResponse;
import com.tatsujin.recipe.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeListActivity extends BaseActivity {

    private static final String TAG = "RecipeListActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // testing search call...
                testRecipeSearch();

                // testing recipe call...
                testGetRecipe();


            }
        });
    }



    private void testRecipeSearch(){
        RecipeApi recipeAPi = ServiceGenerator.getRecipeApi();

        Call<RecipeSearchResponse> responseCall = recipeAPi.searchRecipe(Constants.API_KEY, "chicke breast" , "1");

        responseCall.enqueue(new Callback<RecipeSearchResponse>() {
            @Override
            public void onResponse(Call<RecipeSearchResponse> call, Response<RecipeSearchResponse> response) {
                Log.d(TAG , "onResponse::Server-Search::" + response.body().toString());
                if(response.code() ==200){
                    Log.d(TAG , "onResponse::Search::" + response.body().getRecipes().toString());
                    List<Recipe> recipes = new ArrayList<>(response.body().getRecipes());
                    for(Recipe recipe : recipes){
                        Log.d(TAG , "onResponse: "+ recipe.getTitle());
                    }
                }else{
                    try{
                        Log.d(TAG , "onResponse: "+ response.errorBody().string());
                    }catch(IOException ioe){
                        ioe.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<RecipeSearchResponse> call, Throwable t) {

            }
        });

    }


    private void testGetRecipe(){
            RecipeApi api  = ServiceGenerator.getRecipeApi() ;

            Call<RecipeResponse> recipeResponseCall = api.getRecipe(Constants.API_KEY ,"8c0314" );
            recipeResponseCall.enqueue(new Callback<RecipeResponse>() {
                @Override
                public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                    Log.d(TAG , "onResponse-Server-recipe" + response.body().toString() );
                    if(response.code() == 200){
                        Recipe recipe = response.body().getRecipe() ;
                        Log.d(TAG , "onResponse-Recipe :"+ recipe);
                    }else{
                        try{
                            Log.e(TAG , "onResponse: " + response.errorBody().string());
                        }catch(IOException ioe){
                            ioe.printStackTrace();
                        }
                    }

                }

                @Override
                public void onFailure(Call<RecipeResponse> call, Throwable t) {

                }
            });





    }

}