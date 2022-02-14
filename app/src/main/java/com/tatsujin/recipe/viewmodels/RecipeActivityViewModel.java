package com.tatsujin.recipe.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.tatsujin.recipe.models.Recipe;
import com.tatsujin.recipe.repositories.RecipeRepository;
import com.tatsujin.recipe.utils.Resource;

public class RecipeActivityViewModel extends AndroidViewModel {

    private RecipeRepository recipeRepository ;


    public RecipeActivityViewModel(Application application) {
    super(application);
    recipeRepository = RecipeRepository.getInstance(application);

    }

    public LiveData<Resource<Recipe>> searchRecipeAPI(String recipeID){
        return recipeRepository.searchRecipeAPI(recipeID);
    }





}
