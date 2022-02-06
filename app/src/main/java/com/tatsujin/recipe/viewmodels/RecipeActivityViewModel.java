package com.tatsujin.recipe.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.tatsujin.recipe.models.Recipe;
import com.tatsujin.recipe.repositories.RecipeRepository;

public class RecipeActivityViewModel extends ViewModel {

    private RecipeRepository mRecipeRepository ;
    private String mRecipeID  ;

    private boolean mGotRecipe ;

    public RecipeActivityViewModel() {
//        mRecipeRepository = RecipeRepository.getInstance() ;
        this.mGotRecipe = false ;
    }

    public LiveData<Recipe> getRecipe(){
        return mRecipeRepository.getRecipe();
    }

    public void getDetails(String id){
        this.mRecipeID = id ;
        mRecipeRepository.getDetails(id);
    }
    public String getRecipeID(){
        return this.mRecipeID ;
    }

    public LiveData<Boolean> isRecipeRequestTimedOut(){return mRecipeRepository.isRecipeRequestTimedOut();}



    public boolean ismGotRecipe() {
        return mGotRecipe;
    }

    public void setmGotRecipe(boolean mGotRecipe) {
        this.mGotRecipe = mGotRecipe;
    }
}
