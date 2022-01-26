package com.tatsujin.recipe.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.tatsujin.recipe.models.Recipe;
import com.tatsujin.recipe.repositories.RecipeRepository;

import java.util.List;

public class RecipeListViewModel extends ViewModel {


    private RecipeRepository mRecipeRepository;
    private boolean mIsViewingRecipies ;
    private boolean isQuerying ;
    private static final String TAG = "RecipeListViewModel";



    public RecipeListViewModel() {
        mRecipeRepository = RecipeRepository.getInstance() ;
//        this.mIsViewingRecipies = false ;
        this.isQuerying = false ;
    }

    // querying is (performing a query)...
    public void setIsQuerying(boolean isQuerying){
        this.isQuerying = isQuerying ;
    }
    // querying is (performing a query)...
    public boolean isQuerying(){
        return this.isQuerying ;
    }

    public LiveData<List<Recipe>> getRecipes(){return mRecipeRepository.getRecipes();}

    public void searchRecipesApi(String query , int pageNo){
        this.mIsViewingRecipies = true ;
        this.isQuerying = true ;
        mRecipeRepository.searchRecipesApi(query , pageNo);
    }


    public boolean getIsViewingRecipes(){
        return this.mIsViewingRecipies ;
    }


    public void setIsViewingRecipies(boolean isViewingRecipies){
        this.mIsViewingRecipies = isViewingRecipies ;
    }
    public boolean onBackPressed(){
        if(isQuerying) {
            // cancel the query...
            mRecipeRepository.cancelRequest();
            isQuerying = false;
        }
        if(mIsViewingRecipies){
            Log.d(TAG,"displaying recipies - on back button pressed...");
            mIsViewingRecipies = false ;
            return false ;
        }

        Log.d(TAG,"displaying categories - on back button pressed...");
        return true ;
    }

    public void nextPage(){
        if(!isQuerying && mIsViewingRecipies){
            mRecipeRepository.nextPage();
        }
    }

}
