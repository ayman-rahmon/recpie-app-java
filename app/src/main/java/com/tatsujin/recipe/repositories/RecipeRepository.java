package com.tatsujin.recipe.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.tatsujin.recipe.models.Recipe;
import com.tatsujin.recipe.requests.RecipeAPIClient;

import java.util.List;

public class RecipeRepository {

    private static RecipeRepository instance;
    private RecipeAPIClient mRecipeAPIClient ;
    private String mQuery ;
    private int mPageNo ;
    private MutableLiveData<Boolean> mIsQueryExhausted= new MutableLiveData<>();
    private MediatorLiveData<List<Recipe>> mRecipes = new MediatorLiveData<>();


    public static RecipeRepository getInstance() {
        if (instance == null) {
            instance = new RecipeRepository();
        }
        return instance;
    }

    private RecipeRepository() {
        mRecipeAPIClient = RecipeAPIClient.getInstance() ;
    }

    public LiveData<Boolean> isQueryExhausted(){
        return this.mIsQueryExhausted ;
    }


    private void initMediators(){
        LiveData<List<Recipe>> recipeListAPISource = mRecipeAPIClient.getRecipes();
        mRecipes.addSource(recipeListAPISource, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                if(recipes != null){
                    mRecipes.setValue(recipes);
                    doneQuery(recipes);
                }else{
                    // search database cache
                    doneQuery(null);
                }
            }
        });
    }
    private void doneQuery(List<Recipe> list){
        if(list != null){
            if(list.size() % 30 != 0){
                mIsQueryExhausted.setValue(true);
            }
        }else {
            mIsQueryExhausted.setValue(true);
        }
    }
    public LiveData<List<Recipe>> getRecipes() {
        return mRecipes ;
    }
    public LiveData<Recipe> getRecipe(){
        return mRecipeAPIClient.getRecipe();
    }

    public void searchRecipesApi(String query , int pageNo){
        if(pageNo == 0){ pageNo = 1 ;}
        this.mQuery = query ;
        this.mPageNo = pageNo ;
        this.mIsQueryExhausted.setValue(false); ;
        mRecipeAPIClient.searchRecipesApi(query , pageNo);
    }

    public void getDetails(String id){
        mRecipeAPIClient.getDetails(id);
    }

    public LiveData<Boolean> isRecipeRequestTimedOut(){return mRecipeAPIClient.isRecipeRequestTimedOut() ; }



    public void nextPage(){
        searchRecipesApi(mQuery , mPageNo+1);
    }
    public void cancelRequest() {
        mRecipeAPIClient.cancelSearchRequst();
    }
}
