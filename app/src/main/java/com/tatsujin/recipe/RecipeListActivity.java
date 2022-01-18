package com.tatsujin.recipe;


import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tatsujin.recipe.adapters.RecipeRecyclerAdapter;
import com.tatsujin.recipe.adapters.onRecipeListener;
import com.tatsujin.recipe.models.Recipe;
import com.tatsujin.recipe.requests.RecipeApi;
import com.tatsujin.recipe.requests.ServiceGenerator;
import com.tatsujin.recipe.requests.responses.RecipeResponse;
import com.tatsujin.recipe.utils.Constants;
import com.tatsujin.recipe.utils.Testing;
import com.tatsujin.recipe.viewmodels.RecipeListViewModel;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeListActivity extends BaseActivity implements onRecipeListener {

    private static final String TAG = "RecipeListActivity";
    private RecipeListViewModel mRecipeListViewModel ;
    private RecyclerView mRecyclerView ;
    private RecipeRecyclerAdapter adapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        mRecyclerView = findViewById(R.id.recipe_list);
        mRecipeListViewModel = new ViewModelProvider(this).get(RecipeListViewModel.class) ;

        subscribeObservers();
        initRecyclerView();
        testRecipeSearch();
        initSearchView();


        if(!mRecipeListViewModel.getIsViewingRecipes()){
            // display Categories...
            displaySearchCategories();
        }

    }



    private void initRecyclerView(){
        adapter = new RecipeRecyclerAdapter(this);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void subscribeObservers(){
        mRecipeListViewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                if(recipes != null){
                    Testing.printRecipes(recipes , TAG);
                    adapter.setRecipes(recipes);
                }
            }
        });
    }

    private void searchRecipesApi(String query , int pageNo){
        mRecipeListViewModel.searchRecipesApi(query , pageNo);
    }

    private void initSearchView(){
        final SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.displayLoading();
                mRecipeListViewModel.searchRecipesApi(query , 1);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
    }


    private void displaySearchCategories(){
        mRecipeListViewModel.setIsViewingRecipies(false);
        adapter.displayCategories();
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

    @Override
    public void onRecipeClick(int position) {

    }

    @Override
    public void onCategoryClick(String category) {
        adapter.displayLoading();
        mRecipeListViewModel.searchRecipesApi(category,1);
    }
}