package com.tatsujin.recipe.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tatsujin.recipe.models.Recipe;
import com.tatsujin.recipe.requests.RecipeAPIClient;

import java.util.List;

public class RecipeRepository {

    private static RecipeRepository instance;
    private RecipeAPIClient mRecipeAPIClient ;
    private String mQuery ;
    private int mPageNo ;

    public static RecipeRepository getInstance() {
        if (instance == null) {
            instance = new RecipeRepository();
        }
        return instance;
    }

    private RecipeRepository() {
        mRecipeAPIClient = RecipeAPIClient.getInstance() ;
    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRecipeAPIClient.getRecipes() ;
    }

    public void searchRecipesApi(String query , int pageNo){
        if(pageNo == 0){ pageNo = 1 ;}
        this.mQuery = query ;
        this.mPageNo = pageNo ;

        mRecipeAPIClient.searchRecipesApi(query , pageNo);
    }

    public void nextPage(){
        searchRecipesApi(mQuery , mPageNo+1);
    }
    public void cancelRequest() {
        mRecipeAPIClient.cancelRequst();
    }
}
