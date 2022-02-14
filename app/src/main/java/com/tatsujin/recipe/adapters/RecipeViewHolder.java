package com.tatsujin.recipe.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.tatsujin.recipe.R;
import com.tatsujin.recipe.models.Recipe;

public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView title ;
    public TextView publisher ;
    public AppCompatImageView image ;
    public TextView socialScore ;
    private onRecipeListener onRecipeListener ;
    private RequestManager requestManager ;
    private ViewPreloadSizeProvider preloadSizeProvider ;

    public RecipeViewHolder(@NonNull View itemView
            , com.tatsujin.recipe.adapters.onRecipeListener mOnRecipeListener
            , RequestManager requestManager
            , ViewPreloadSizeProvider preloadSizeProvider) {
        super(itemView);
        this.onRecipeListener = mOnRecipeListener ;
        this.requestManager = requestManager ;
        this.preloadSizeProvider = preloadSizeProvider ;
        title = itemView.findViewById(R.id.recipe_title);
        publisher = itemView.findViewById(R.id.recipe_publisher);
        image = itemView.findViewById(R.id.recipe_image);
        socialScore = itemView.findViewById(R.id.recipe_social_score);
        itemView.setOnClickListener(this);
    }

    public void onBind(Recipe recipe){

                requestManager.load(recipe.getImage_url())
                .into(image);

        title.setText(recipe.getTitle());
        publisher.setText(recipe.getPublisher());
        socialScore.setText(String.valueOf(Math.round(recipe.getSocial_rank())));

        preloadSizeProvider.setView(image);

    }

    @Override
    public void onClick(View view) {

        onRecipeListener.onRecipeClick(getAbsoluteAdapterPosition());


    }

}
