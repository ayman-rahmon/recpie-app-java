package com.tatsujin.recipe.repositories;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.tatsujin.recipe.models.Recipe;
import com.tatsujin.recipe.persistence.RecipeDAO;
import com.tatsujin.recipe.persistence.RecipeDatabase;
import com.tatsujin.recipe.requests.ServiceGenerator;
import com.tatsujin.recipe.requests.responses.ApiResponse;
import com.tatsujin.recipe.requests.responses.RecipeSearchResponse;
import com.tatsujin.recipe.utils.AppExecutors;
import com.tatsujin.recipe.utils.Constants;
import com.tatsujin.recipe.utils.NetworkBoundResource;
import com.tatsujin.recipe.utils.Resource;

import java.util.List;

public class RecipeRepository {

    private static RecipeRepository instance;
    private RecipeDAO recipeDAO ;
    private String mQuery ;
    private int mPageNo ;
    private MutableLiveData<Boolean> mIsQueryExhausted= new MutableLiveData<>();
    private MediatorLiveData<List<Recipe>> mRecipes = new MediatorLiveData<>();

    private static final String TAG = "RecipeRepository";
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
                if(item.getRecipes() != null) {
                    Recipe[] recipes = new Recipe[item.getRecipes().size()];

                    int index = 0 ;
                    // insert recipes in the db with keeping in mind to ignore
                    for(long rowid :recipeDAO.insertRecipes((Recipe[]) item.getRecipes().toArray(recipes))){
                            // if the recipe already exist ... don't want to set the ingredients or timestamp
                            // they will be erased...
                        if(rowid == -1){ // there is a conflict .... update instead...
                            Log.d(TAG , "conflict ..." );
                            recipeDAO.updateRecipe(
                                    recipes[index].getRecipe_id() ,
                                    recipes[index].getTitle() ,
                                    recipes[index].getPublisher() ,
                                    recipes[index].getImage_url() ,
                                    recipes[index].getSocial_rank()
                                    );
                        }
                        index++;
                    }

                }



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


                return ServiceGenerator.getRecipeApi().searchRecipe(Constants.API_KEY , query , String.valueOf(pageNumber));
            }
        }.getAsLiveData();
    }


}
