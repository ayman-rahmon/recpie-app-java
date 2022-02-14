package com.tatsujin.recipe.adapters;

import android.app.DownloadManager;
import android.app.RemoteInput;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.tatsujin.recipe.R;
import com.tatsujin.recipe.models.Recipe;
import com.tatsujin.recipe.utils.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ListPreloader.PreloadModelProvider<String> {

    private static final int RECIPE_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int CATEGORIES_TYPE = 3;
    private static final int EXHAUSTED_TYPE = 4;

    private static final String TAG = "RecipeRecyclerAdapter";
    private List<Recipe> mRecipes;

    private onRecipeListener mOnRecipeListener;

    private RequestManager requestManager ;

    private ViewPreloadSizeProvider<String> preloadSizeProvider ;

    public RecipeRecyclerAdapter(onRecipeListener mOnRecipeListener , RequestManager requestManager , ViewPreloadSizeProvider<String> preloadSizeProvider) {
        this.mOnRecipeListener = mOnRecipeListener;
        this.requestManager = requestManager ;
        this.preloadSizeProvider = preloadSizeProvider ;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;

        switch (viewType) {
            case RECIPE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recipe_list_item, parent, false);
                return new RecipeViewHolder(view, mOnRecipeListener ,requestManager , preloadSizeProvider);
            case LOADING_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_list_item, parent, false);
                return new LoadingViewHolder(view);
            case CATEGORIES_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category_list_item, parent, false);
                return new CategoryViewHolder(view, mOnRecipeListener , requestManager);
            case EXHAUSTED_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_exhausted, parent, false);
                return new SearchExhaustedViewHolde(view);

            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recipe_list_item, parent, false);
                return new RecipeViewHolder(view, mOnRecipeListener ,requestManager , preloadSizeProvider);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        int itemViewType = getItemViewType(position);

        if (itemViewType == RECIPE_TYPE) {
            ((RecipeViewHolder)holder).onBind(mRecipes.get(position));
        } else if (itemViewType == CATEGORIES_TYPE) {
            ((CategoryViewHolder)holder).onBind(mRecipes.get(position));
        }


    }

    @Override
    public int getItemViewType(int position) {
        if(mRecipes.get(position).getSocial_rank() == -1){
            return CATEGORIES_TYPE;
        }
        else if (mRecipes.get(position).getTitle().equals("LOADING")) {
            return LOADING_TYPE;
        }else if(mRecipes.get(position).getTitle().equals("EXHAUSTED")){
            return EXHAUSTED_TYPE ;
        } else {
            return RECIPE_TYPE;
        }

    }


    // display loading during search request...
    public void displayOnlyLoading(){
        clearRecipesList();
        Recipe recipe = new Recipe() ;
        recipe.setTitle("LOADING");
        mRecipes.add(recipe);
        notifyDataSetChanged();
    }

    private void clearRecipesList(){
        if(mRecipes == null){
            mRecipes = new ArrayList<>();
        }else{
            mRecipes.clear();
        }
        notifyDataSetChanged();
    }
    public void setQueryEhausted(){
        hideLoading();
        Recipe exhaustedRecipe = new Recipe();
        exhaustedRecipe.setTitle("EXHAUSTED");
        mRecipes.add(exhaustedRecipe);
        notifyDataSetChanged();

    }

    public void hideLoading() {
        if(isLoading()){
            if(mRecipes.get(0).getTitle().equals("LOADING")){
                mRecipes.remove(0);
            } else if(mRecipes.get(mRecipes.size()-1).equals("LOADING")){
                mRecipes.remove(mRecipes.size()-1);
            }
            notifyDataSetChanged();
        }
    }


    public Recipe getSelectedRecipe(int position) {
        if(mRecipes != null){
            if(mRecipes.size() > 0){
                return mRecipes.get(position);
            }
        }
        return null ;
    }

    public void displayCategories(){
        List<Recipe> categories = new ArrayList<>();
        for(int i = 0 ; i < Constants.DEFAULT_SEARCH_CATEGORIES.length  ; i++){
            Recipe recipe = new Recipe();
            recipe.setTitle(Constants.DEFAULT_SEARCH_CATEGORIES[i]);
            recipe.setImage_url(Constants.DEFAULT_SEARCH_CATEGORY_IMAGES[i]);
            recipe.setSocial_rank(-1);
            categories.add(recipe);
        }
        mRecipes = categories ;
        notifyDataSetChanged();
    }


    // pagination... loading...
    public void displayLoading() {
        if(mRecipes == null){
            mRecipes = new ArrayList<>();
        }
        if (!isLoading()) {
            Recipe recipe = new Recipe();
            recipe.setTitle("LOADING");
            mRecipes.add(recipe);
            notifyDataSetChanged();
        }
    }

    private boolean isLoading() {
        if (mRecipes != null) {
            if (mRecipes.size() > 0) {
                if (mRecipes.get(mRecipes.size() - 1).getTitle().equals("LOADING")) {
                    return true;
                }
            }
        }
        return false;
    }


    public void setRecipes(List<Recipe> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mRecipes != null) {
            return mRecipes.size();
        }
        return 0;
    }

    @NonNull
    @Override
    public List<String> getPreloadItems(int position) {
        String url = mRecipes.get(position).getImage_url();
        if(TextUtils.isEmpty(url)){
            return Collections.emptyList();
        }
        return Collections.singletonList(url);
    }

    @Nullable
    @Override
    public RequestBuilder<?> getPreloadRequestBuilder(@NonNull String item) {
        return requestManager.load(item);
    }
}
