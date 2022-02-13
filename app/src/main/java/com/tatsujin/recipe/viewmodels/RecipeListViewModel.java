package com.tatsujin.recipe.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.tatsujin.recipe.models.Recipe;
import com.tatsujin.recipe.repositories.RecipeRepository;
import com.tatsujin.recipe.utils.Resource;

import java.util.List;

public class RecipeListViewModel extends AndroidViewModel {

    private static final String TAG = "RecipeListViewModel";
    public enum ViewState {CATEGORIES , RECIPES} ;
    private MutableLiveData<ViewState> viewState ;
    private MediatorLiveData<Resource<List<Recipe>>> recipes = new MediatorLiveData<>();
    private RecipeRepository recipeRepository ;
    public RecipeListViewModel(Application application){
        super(application);
        recipeRepository = RecipeRepository.getInstance(application);
        init();
    }

    private void init(){
        if(viewState == null){
            viewState = new MutableLiveData<>();
            viewState.setValue(ViewState.CATEGORIES);
        }
    }


    public LiveData<ViewState> getViewState(){
        return viewState ;
    }

    public void searchRecipesAPI(String query , int pageNumber){
        final LiveData<Resource<List<Recipe>>> repositorySource = recipeRepository.searchRecipesAPI(query,pageNumber);
        recipes.addSource(repositorySource, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(Resource<List<Recipe>> listResource) {
                //(TODO) react to the data before setting it...
                recipes.setValue(listResource);
            }
        });
    }

    public LiveData<Resource<List<Recipe>>> getRecipes(){
        return recipes ;
    }

}
