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

import java.time.LocalDate;
import java.util.List;

public class RecipeListViewModel extends AndroidViewModel {

    private static final String TAG = "RecipeListViewModel";
    public enum ViewState {CATEGORIES , RECIPES} ;
    private MutableLiveData<ViewState> viewState ;
    private MediatorLiveData<Resource<List<Recipe>>> recipes = new MediatorLiveData<>();
    private RecipeRepository recipeRepository ;
    public static final String QUERY_EXHAUSTED = "no more results.";

    // query extras...
    private boolean isQueryExhausted ;
    private boolean isPerformingQuery ;
    private int pageNo ;
    private String query;
    private boolean cancelRequest;
    private long requestStartTime ;



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
        if(!isPerformingQuery){
            if(pageNumber == 0 ){
                pageNumber = 1 ;
            }
            this.pageNo = pageNumber ;
            this.query = query ;
            executeSearch();

        }
    }

    private void executeSearch(){
        requestStartTime = System.currentTimeMillis();
        cancelRequest = false ;
        isPerformingQuery = true ;
        viewState.setValue(ViewState.RECIPES);
        final LiveData<Resource<List<Recipe>>> repositorySource = recipeRepository.searchRecipesAPI(query,pageNo);
        recipes.addSource(repositorySource, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(Resource<List<Recipe>> listResource) {
                if(!cancelRequest){
                    if(listResource !=null){
                        recipes.setValue(listResource);
                        if(listResource.status == Resource.Status.SUCCESS){
                            Log.d(TAG , "onChanged: Request time : "+(System.currentTimeMillis() - requestStartTime)/1000 + "seconds");

                            isPerformingQuery =false ;
                            if(listResource.data !=null){
                                if(listResource.data.size() ==  0){
                                    Log.d(TAG , "onChange: query is exhausted...");
                                    recipes.setValue(
                                            new Resource<List<Recipe>>(Resource.Status.ERROR ,
                                                    listResource.data ,
                                                    QUERY_EXHAUSTED));
                                }
                            }
                            recipes.removeSource(repositorySource);
                        }else if(listResource.status == Resource.Status.ERROR){
                            Log.d(TAG , "onChanged: Request time : "+(System.currentTimeMillis() - requestStartTime)/1000 + "seconds");
                            isPerformingQuery =false ;
                            recipes.removeSource(repositorySource);
                        }
                    }else {
                        recipes.removeSource(repositorySource);
                    }

                }else {
                    recipes.removeSource(repositorySource);
                }
            }
        });

    }

    public void cancelSearchRequest(){
        if(isPerformingQuery){
            Log.d(TAG, "cancelSearchRequest: cancelling the search request." );
            cancelRequest =true ;
            isPerformingQuery = false ;
            pageNo = 1 ;
        }
    }

    public void setViewCategories(){
        viewState.setValue(ViewState.CATEGORIES);
    }

    public void searchNextPage(){
        if(!isQueryExhausted && !isPerformingQuery){
            pageNo++;
            executeSearch();
        }
    }
    public LiveData<Resource<List<Recipe>>> getRecipes(){
        return recipes ;
    }

    public int getPageNo() {
        return pageNo;
    }
}
