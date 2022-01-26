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
        subscribeObservers();
        getIncomingIntent();

    }


    private void subscribeObservers(){
        mrecipeActivityViewModel.getRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(Recipe recipe) {
                if(recipe != null){
                    if(recipe.getRecipe_id().equals(mrecipeActivityViewModel.getRecipeID())){
                        setRecipeProperties(recipe);
                        mrecipeActivityViewModel.setmGotRecipe(true);
                    }
                }
            }
        });
        mrecipeActivityViewModel.isRecipeRequestTimedOut().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean && !mrecipeActivityViewModel.ismGotRecipe()){
                    Log.d(TAG , "onChanged: timed out...");
                }
            }
        });

    }


    private void getIncomingIntent(){
        if(getIntent().hasExtra("recipe")){
            Recipe recipe = getIntent().getParcelableExtra("recipe");
            Log.d(TAG , "getIncomingIntent ::" +  recipe.getTitle());
            mrecipeActivityViewModel.getDetails(recipe.getRecipe_id());
        }
    }

    private void setRecipeProperties(Recipe recipe ){
        if(recipe != null){
            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_launcher_background);
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
