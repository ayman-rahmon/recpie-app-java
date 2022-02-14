package com.tatsujin.recipe;

import android.app.DownloadManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tatsujin.recipe.models.Recipe;
import com.tatsujin.recipe.utils.Resource;
import com.tatsujin.recipe.viewmodels.RecipeActivityViewModel;

public class RecipeActivity extends BaseActivity{

    // UI
    private AppCompatImageView mImage ;
    private TextView mTitle , mRank  ;
    private LinearLayout mRecipeIngredientsContainer ;
    private ScrollView mScrollView ;
    private static final String TAG = "RecipeActivity";

    private RecipeActivityViewModel mrecipeActivityViewModel ;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_recipe_activity);
        mImage = findViewById(R.id.recipe_image);
        mTitle = findViewById(R.id.recipe_title);
        mRank = findViewById(R.id.recipe_social_score);
        mRecipeIngredientsContainer = findViewById(R.id.ingredients_container);
        mScrollView = findViewById(R.id.parent);

        mrecipeActivityViewModel = new ViewModelProvider(this).get(RecipeActivityViewModel.class);
        showProgressBar(true);
        getIncomingIntent();

    }


    private void subscribeObservers(final String recipeID){
        mrecipeActivityViewModel.searchRecipeAPI(recipeID).observe(this, new Observer<Resource<Recipe>>() {
            @Override
            public void onChanged(Resource<Recipe> recipeResource) {
                if(recipeResource != null){
                    if(recipeResource.data != null){
                        switch(recipeResource.status){
                            case LOADING:{
                                showProgressBar(true);
                                break ;
                            }
                            case ERROR: {
                                Log.e(TAG , "onChanged: status ERROR , Recipe : " + recipeResource.data.getTitle());
                                Log.e(TAG , "onChanged: ERROR Message :" + recipeResource.message);
                                showParent();
                                showProgressBar(false);
                                setRecipeProperties(recipeResource.data);
                                break;
                            }
                            case SUCCESS: {
                                Log.d(TAG, "onChanged : cache has been refreshed.");
                                Log.d(TAG , "onChanged : status: SUCCESS , Recipe" + recipeResource.data.getTitle() );
                                showParent();
                                showProgressBar(false);
                                setRecipeProperties(recipeResource.data);
                                break;
                            }
                        }
                    }
                }
            }
        });


    }




    private void getIncomingIntent(){
        if(getIntent().hasExtra("recipe")){
            Recipe recipe = getIntent().getParcelableExtra("recipe");
            Log.d(TAG , "getIncomingIntent ::" +  recipe.getTitle());
            subscribeObservers(recipe.getRecipe_id());
        }
    }

    private void displayErrorScreen(String errorMessage){
        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_launcher_background);
        Glide.with(this).setDefaultRequestOptions(requestOptions).load(R.drawable.ic_launcher_background).into(mImage);

        mTitle.setText("Error retrieving recipe...");
        mRank.setText("");
        TextView textView = new TextView(this);
        if(!errorMessage.equals("")){
            textView.setText(errorMessage);
        }else{
            textView.setText("Error");
        }
        textView.setTextSize(15);
        textView.setTextSize(15);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT));
        mRecipeIngredientsContainer.addView(textView);
        showParent();
        showProgressBar(false);
    }

    private void setRecipeProperties(Recipe recipe ){
        if(recipe != null){
            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.white_background).error(R.drawable.white_background);
            Glide.with(this).setDefaultRequestOptions(requestOptions).load(recipe.getImage_url()).into(mImage);
            mTitle.setText(recipe.getTitle());
            mRank.setText(String.valueOf(Math.round(recipe.getSocial_rank())));
            mRecipeIngredientsContainer.removeAllViews();
            for(String ingredient : recipe.getIngredients()){
                TextView textView = new TextView(this);
                textView.setText(ingredient);
                textView.setTextSize(15);
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT));
                mRecipeIngredientsContainer.addView(textView);
            }
        }
        showParent();
        showProgressBar(false);
    }

    private void showParent(){
        mScrollView.setVisibility(View.VISIBLE);
    }



}
