package com.tatsujin.recipe.requests;

import static com.tatsujin.recipe.utils.Constants.NETWORK_TIMEOUT;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tatsujin.recipe.models.Recipe;
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


    private RetrieveRecipesRunnable mRetrieveRecipesRunnable ;
    public static RecipeAPIClient getInstance(){
        if(instance == null){
            instance = new RecipeAPIClient() ;
        }
        return instance ;
    }


    private RecipeAPIClient(){
        mRecipes = new MutableLiveData<>();
    }

    public LiveData<List<Recipe>> getRecipes(){
        return mRecipes ;
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




    public void cancelRequst(){
        if(mRetrieveRecipesRunnable != null) {
            mRetrieveRecipesRunnable.cancelRequest();
        }
    }


}
