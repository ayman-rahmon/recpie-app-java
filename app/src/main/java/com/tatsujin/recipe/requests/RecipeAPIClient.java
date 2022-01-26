package com.tatsujin.recipe.requests;

import static com.tatsujin.recipe.utils.Constants.NETWORK_TIMEOUT;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tatsujin.recipe.models.Recipe;
import com.tatsujin.recipe.requests.responses.RecipeResponse;
import com.tatsujin.recipe.requests.responses.RecipeSearchResponse;
import com.tatsujin.recipe.utils.AppExecutors;
import com.tatsujin.recipe.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class RecipeAPIClient {

    private static final String TAG = "RecipeAPIClient";
    private static RecipeAPIClient instance ;

    private MutableLiveData<List<Recipe>> mRecipes ;
    private MutableLiveData<Recipe> mRecipe ;

    private RetrieveRecipesRunnable mRetrieveRecipesRunnable ;

    private RetrieveRecipeRunnable mRetrieveRecipeRunnable ;



    private MutableLiveData<Boolean> mRecipeRequestTimeout ;



    public static RecipeAPIClient getInstance(){
        if(instance == null){
            instance = new RecipeAPIClient() ;
        }
        return instance ;
    }


    private RecipeAPIClient(){
        mRecipes = new MutableLiveData<>();
        mRecipe = new MutableLiveData<>();
    }

    public LiveData<List<Recipe>> getRecipes(){
        return mRecipes ;
    }

    public LiveData<Boolean> isRecipeRequestTimedOut(){return mRecipeRequestTimeout ; }

    public LiveData<Recipe> getRecipe(){
        return mRecipe ;
    }
    public void searchRecipesApi(String query , int pageNo) {
        if(mRetrieveRecipesRunnable != null) {
            mRetrieveRecipesRunnable = null ;
        }
        mRetrieveRecipesRunnable = new RetrieveRecipesRunnable(query , pageNo);
        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrieveRecipesRunnable);

        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // TODO(1) ==> let the user know it has timed out...
                handler.cancel(true);
            }
        } , NETWORK_TIMEOUT , TimeUnit.MILLISECONDS);
    }

    public void getDetails(String id){
        if(mRetrieveRecipeRunnable != null){
            mRetrieveRecipeRunnable = null ;
        }
        mRetrieveRecipeRunnable = new RetrieveRecipeRunnable(id);
        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrieveRecipeRunnable);
        mRecipeRequestTimeout.setValue(false);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // TODO (3) update UI...
                mRecipeRequestTimeout.postValue(true);
                handler.cancel(true);
            }
        },NETWORK_TIMEOUT , TimeUnit.MILLISECONDS);

    }


    private class RetrieveRecipesRunnable implements Runnable {
        private String query ;
        private int pageNo;
        private boolean cancelRequest ;


        public RetrieveRecipesRunnable(String query, int pageNo) {
            this.query = query;
            this.pageNo = pageNo;
            this.cancelRequest = false ;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipes(query , pageNo ).execute() ;
                if(cancelRequest) {
                    return;
                }
                if(response.code() == 200){
                    Log.d(TAG ,"code is 200" + "response : " + response.body().toString()  );
                    List<Recipe> list = new ArrayList<>(((RecipeSearchResponse)response.body()).getRecipes());
                    if(pageNo == 1) {
                        mRecipes.postValue(list);
                    } else {
                        List<Recipe> currentRecipes = mRecipes.getValue() ;
                        currentRecipes.addAll(list);
                        mRecipes.postValue(currentRecipes);
                    }
                }else{
                    String error = response.errorBody().string();
                    Log.e(TAG , "run: error " + error);
                    mRecipes.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG , "Message :"+e.getMessage()  + "code : " + e.getCause());
                mRecipes.postValue(null);
            }







        }
        private Call<RecipeSearchResponse> getRecipes(String query , int pageNo){
            return ServiceGenerator.getRecipeApi().searchRecipe(Constants.API_KEY , query , String.valueOf(pageNo));

        }

        private void cancelRequest(){
            Log.d(TAG , "request Cancelled...");
            this.cancelRequest = true ;
        }

    }

    private class RetrieveRecipeRunnable implements Runnable{

        private String recipeID ;
        private boolean cancelRequest;
        public  RetrieveRecipeRunnable(String recipeID){
            this.recipeID = recipeID ;
            this.cancelRequest = false ;
        }


        @Override
        public void run() {
            Response response = null;
            try {
                response = getRecipe(recipeID).execute();
                if(cancelRequest){return ;}
                if(response.code() == 200){
                    Recipe recipe = ((RecipeResponse)response.body()).getRecipe() ;
                    mRecipe.postValue(recipe);
                }else {
                    String error  = response.errorBody().string() ;
                    Log.e(TAG , "run:" + error);
                    mRecipe.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mRecipe.postValue(null);
            }
        }

        private Call<RecipeResponse> getRecipe(String recipeID){
            return ServiceGenerator.getRecipeApi().getRecipe(Constants.API_KEY , recipeID);
        }

    }



    public void cancelSearchRequst(){
        if(mRetrieveRecipesRunnable != null) {
            mRetrieveRecipesRunnable.cancelRequest();
        }

    }


}
