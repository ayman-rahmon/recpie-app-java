package com.tatsujin.recipe.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.tatsujin.recipe.models.Recipe;
import com.tatsujin.recipe.persistence.RecipeDAO;
import com.tatsujin.recipe.persistence.RecipeDatabase;
import com.tatsujin.recipe.requests.RecipeAPIClient;
import com.tatsujin.recipe.requests.responses.ApiResponse;
import com.tatsujin.recipe.requests.responses.RecipeSearchResponse;
import com.tatsujin.recipe.utils.AppExecutors;
import com.tatsujin.recipe.utils.NetworkBoundResource;
import com.tatsujin.recipe.utils.Resource;

import java.util.List;

public class RecipeRepository {

    private static RecipeRepository instance;
    private RecipeDAO recipeDAO ;
    private RecipeAPIClient mRecipeAPIClient ;
    private String mQuery ;
    private int mPageNo ;
    private MutableLiveData<Boolean> mIsQueryExhausted= new MutableLiveData<>();
    private MediatorLiveData<List<Recipe>> mRecipes = new MediatorLiveData<>();


    public static RecipeRepository getInstance(Context context) {
        if (instance == null) {
            instance = new RecipeRepository(context);
        }
        return instance;
    }

    private RecipeRepository(Context context){
        recipeDAO = RecipeDatabase.getInstance(context).getRecipeDao() ;
    }

    public LiveData<Resource<List<Recipe>>> searchRecipesAPI(final String query , final int pageNumber){
        return new NetworkBoundResource<List<Recipe> , RecipeSearchResponse>(AppExecutors.getInstance()){
            @Override
            protected void saveCallResult(@NonNull RecipeSearchResponse item) {

            }

            @Override
            protected boolean shouldFetch(@Nullable List<Recipe> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Recipe>> loadFromDB() {
                return recipeDAO.searchRecipe(query,pageNumber);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<RecipeSearchResponse>> createCall() {


                return null;
            }
        }.getAsLiveData();
    }


}
