package com.tatsujin.recipe.adapters;

import android.app.DownloadManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tatsujin.recipe.R;
import com.tatsujin.recipe.models.Recipe;

import java.util.List;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<Recipe> mRecipes ;

    private onRecipeListener mOnRecipeListener ;


    public RecipeRecyclerAdapter(List<Recipe> mRecipes, onRecipeListener mOnRecipeListener) {
        this.mRecipes = mRecipes;
        this.mOnRecipeListener = mOnRecipeListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_recipe_list,parent, false);



        return new RecipeViewHolder(view , mOnRecipeListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((RecipeViewHolder)holder).title.setText(mRecipes.get(position).getTitle());
        ((RecipeViewHolder)holder).publisher.setText(mRecipes.get(position).getPublisher());
        ((RecipeViewHolder)holder).socialScore.setText(String.valueOf(Math.round(mRecipes.get(position).getSocial_rank())));

    }

    public void setmRecipes(List<Recipe> recipes){
        mRecipes = recipes ;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return mRecipes.size();
    }
}
