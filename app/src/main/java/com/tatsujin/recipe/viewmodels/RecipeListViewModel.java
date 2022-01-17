package com.tatsujin.recipe.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.tatsujin.recipe.models.Recipe;
import com.tatsujin.recipe.repositories.RecipeRepository;

import java.util.List;

public class RecipeListViewModel extends ViewModel {


    private RecipeRepository mRecipeRepository;

    public RecipeListViewModel() {
        mRecipeRepository = RecipeRepository.getInstance() ;
    }

    public LiveData<List<Recipe>> getRecipes(){return mRecipeRepository.getRecipes();}


}
