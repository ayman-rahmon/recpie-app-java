package com.tatsujin.recipe.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tatsujin.recipe.models.Recipe;
import com.tatsujin.recipe.repositories.RecipeRepository;

import java.util.List;

public class RecipeListViewModel extends AndroidViewModel {

    private static final String TAG = "RecipeListViewModel";
    public enum ViewState {CATEGORIES , RECIPES} ;
    private MutableLiveData<ViewState> viewState ;

    public RecipeListViewModel(Application application){
        super(application);
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


}
