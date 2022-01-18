package com.tatsujin.recipe.adapters;

import android.app.DownloadManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tatsujin.recipe.R;
import com.tatsujin.recipe.models.Recipe;

import java.util.List;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "RecipeRecyclerAdapter";
    private List<Recipe> mRecipes ;

    private onRecipeListener mOnRecipeListener ;


    public RecipeRecyclerAdapter(onRecipeListener mOnRecipeListener) {
        this.mOnRecipeListener = mOnRecipeListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recipe_list_item,parent, false);


        return new RecipeViewHolder(view , mOnRecipeListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_launcher_background);

//        Log.d(TAG , "image : " +mRecipes.get(position).getImage_url() );
//        Log.d(TAG , "title : " +mRecipes.get(position).getTitle() );
//        Log.d(TAG , "publisher : " +mRecipes.get(position).getPublisher() );
//        Log.d(TAG , "score : " +mRecipes.get(position).getSocial_rank() );

        Glide.with(holder.itemView.getContext())
                .setDefaultRequestOptions(requestOptions)
                .load(mRecipes.get(position).getImage_url())
                .into(((RecipeViewHolder)holder).image);

        ((RecipeViewHolder)holder).title.setText(mRecipes.get(position).getTitle());
        ((RecipeViewHolder)holder).publisher.setText(mRecipes.get(position).getPublisher());
        ((RecipeViewHolder)holder).socialScore.setText(String.valueOf(Math.round(mRecipes.get(position).getSocial_rank())));

    }

    public void setRecipes(List<Recipe> recipes){
        mRecipes = recipes ;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        if(mRecipes != null){
            return mRecipes.size();
        }
        return 0 ;
    }
}
