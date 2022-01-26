package com.tatsujin.recipe.adapters;

import android.app.DownloadManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tatsujin.recipe.R;
import com.tatsujin.recipe.models.Recipe;
import com.tatsujin.recipe.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int RECIPE_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int CATEGORIES_TYPE = 3;

    private static final String TAG = "RecipeRecyclerAdapter";
    private List<Recipe> mRecipes;

    private onRecipeListener mOnRecipeListener;


    public RecipeRecyclerAdapter(onRecipeListener mOnRecipeListener) {
        this.mOnRecipeListener = mOnRecipeListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;

        switch (viewType) {
            case RECIPE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recipe_list_item, parent, false);
                return new RecipeViewHolder(view, mOnRecipeListener);
            case LOADING_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_list_item, parent, false);
                return new LoadingViewHolder(view);
            case CATEGORIES_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category_list_item, parent, false);
                return new CategoryViewHolder(view, mOnRecipeListener);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recipe_list_item, parent, false);
                return new RecipeViewHolder(view, mOnRecipeListener);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        int itemViewType = getItemViewType(position);

        if (itemViewType == RECIPE_TYPE) {
            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_launcher_background);
            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(mRecipes.get(position).getImage_url())
                    .into(((RecipeViewHolder) holder).image);

            ((RecipeViewHolder) holder).title.setText(mRecipes.get(position).getTitle());
            ((RecipeViewHolder) holder).publisher.setText(mRecipes.get(position).getPublisher());
            ((RecipeViewHolder) holder).socialScore.setText(String.valueOf(Math.round(mRecipes.get(position).getSocial_rank())));
        } else if (itemViewType == CATEGORIES_TYPE) {
            RequestOptions requestOption = new RequestOptions().placeholder(R.drawable.ic_launcher_background);
            // TODO(2) fix the uri for displaying the images properly...
            Uri path = Uri.parse("android.resource://com.tatsujin.recipe/drawable/" + mRecipes.get(position).getImage_url());
            Glide.with(holder.itemView.getContext()).setDefaultRequestOptions(requestOption).load(path).into(((CategoryViewHolder) holder).categoryImage);
            ((CategoryViewHolder)holder).categoryTitle.setText(mRecipes.get(position).getTitle());

        }


    }

    @Override
    public int getItemViewType(int position) {
        if(mRecipes.get(position).getSocial_rank() == -1){
            return CATEGORIES_TYPE;
        }
        else if (mRecipes.get(position).getTitle().equals("LOADING")) {
            return LOADING_TYPE;
        }else if(position == mRecipes.size()-1 && position !=0 && !mRecipes.get(position).getTitle().equals("EXHAUSTED")) {
            return LOADING_TYPE ;
        } else {
            return RECIPE_TYPE;
        }

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

    public void displayLoading() {
        if (!isLoading()) {
            Recipe recipe = new Recipe();
            recipe.setTitle("LOADING");
            List<Recipe> loadingList = new ArrayList<>();
            loadingList.add(recipe);
            mRecipes = loadingList;
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
}
