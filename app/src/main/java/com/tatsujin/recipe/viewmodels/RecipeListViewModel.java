package com.tatsujin.recipe.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.tatsujin.recipe.models.Recipe;
import com.tatsujin.recipe.repositories.RecipeRepository;

import java.util.List;

public class RecipeListViewModel extends ViewModel {


    private RecipeRepository mRecipeRepository;
    private boolean mIsViewingRecipies ;

    public RecipeListViewModel() {
        mRecipeRepository = RecipeRepository.getInstance() ;
        this.mIsViewingRecipies = false ;
    }

    public LiveData<List<Recipe>> getRecipes(){return mRecipeRepository.getRecipes();}

    public void searchRecipesApi(String query , int pageNo){
        this.mIsViewingRecipies = true ;
        mRecipeRepository.searchRecipesApi(query , pageNo);
    }


    public boolean getIsViewingRecipes(){
        return this.mIsViewingRecipies ;
    }
    public void setIsViewingRecipies(boolean isViewingRecipies){
        this.mIsViewingRecipies = isViewingRecipies ;
    }
}
