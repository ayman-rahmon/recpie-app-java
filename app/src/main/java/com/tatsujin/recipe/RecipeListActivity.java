package com.tatsujin.recipe;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.tatsujin.recipe.models.Recipe;
import com.tatsujin.recipe.requests.RecipeApi;
import com.tatsujin.recipe.requests.ServiceGenerator;
import com.tatsujin.recipe.requests.responses.RecipeResponse;
import com.tatsujin.recipe.requests.responses.RecipeSearchResponse;
import com.tatsujin.recipe.utils.Constants;
import com.tatsujin.recipe.utils.Testing;
import com.tatsujin.recipe.viewmodels.RecipeListViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeListActivity extends BaseActivity {

    private static final String TAG = "RecipeListActivity";
    private RecipeListViewModel mRecipeListViewModel ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        mRecipeListViewModel = new ViewModelProvider(this).get(RecipeListViewModel.class) ;

        subscribeObservers();

        ((Button)findViewById(R.id.test)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testRecipeSearch();
            }
        });

    }


    private void subscribeObservers(){
        mRecipeListViewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                if(recipes != null){
                    Testing.printRecipes(recipes , TAG);
                }
            }
        });

    }

    private void searchRecipesApi(String query , int pageNo){
        mRecipeListViewModel.searchRecipesApi(query , pageNo);
    }


    private void testRecipeSearch(){
        searchRecipesApi("chicken breast" ,1 );
    }


    private void testGetRecipe(){
            RecipeApi api  = ServiceGenerator.getRecipeApi() ;

            Call<RecipeResponse> recipeResponseCall = api.getRecipe(Constants.API_KEY ,"8c0314" );
            recipeResponseCall.enqueue(new Callback<RecipeResponse>() {
                @Override
                public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                    Log.d(TAG , "onResponse-Server-recipe" + response.toString() );
                    if(response.code() == 200){
                        Recipe recipe = response.body().getRecipe() ;
                        Log.d(TAG , "onResponse-Recipe :"+ recipe.toString());
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